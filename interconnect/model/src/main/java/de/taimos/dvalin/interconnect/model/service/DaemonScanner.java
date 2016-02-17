package de.taimos.dvalin.interconnect.model.service;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.taimos.dvalin.interconnect.model.InterconnectList;
import de.taimos.dvalin.interconnect.model.InterconnectObject;
import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.ivo.daemon.VoidIVO.VoidIVOBuilder;

/**
 * Scans a Daemon Request Handler for {@link DaemonRequestMethod} and {@link DaemonReceiverMethod} methods.
 */
public final class DaemonScanner {

	private static final DaemonMethod NOT_A_DAEMON_METHOD_FLAG = new DaemonMethod(null, null, null, null, false, false);

	private static final Map<Method, DaemonMethod> CACHE = new ConcurrentHashMap<>();

	private static final Logger LOGGER2 = LoggerFactory.getLogger(DaemonScanner.class);


	/**
	 * @param clazz Class to scan
	 * @return Daemon methods
	 */
	public static Set<DaemonMethod> scan(final Class<? extends IDaemonHandler> clazz) {
		final Set<DaemonMethod> res = new HashSet<>();
		for (final Method method : clazz.getMethods()) {
			final DaemonMethod dm = DaemonScanner.scan(method);
			if (dm != null) {
				if (res.contains(dm)) {
					throw new IllegalStateException("duplicate @DaemonRequestMethod");
				}
				res.add(dm);
			}
		}
		return res;
	}

	/**
	 * @param method Method to scan
	 * @return Daemon method or null if not a daemon method
	 */
	public static DaemonMethod scan(final Method method) {
		final DaemonMethod cached = DaemonScanner.CACHE.get(method);
		if (cached != null) {
			if (cached == DaemonScanner.NOT_A_DAEMON_METHOD_FLAG) {
				return null;
			}
			return cached;
		}
		final DaemonMethod dm;
		{
			final DaemonRequestMethod drm = DaemonScanner.getAnnotation(DaemonRequestMethod.class, method);
			if (drm != null) {
				dm = DaemonScanner.scanRequest(method, drm);
				DaemonScanner.CACHE.put(method, dm);
				return dm;
			}
		}
		{
			final DaemonReceiverMethod drm = DaemonScanner.getAnnotation(DaemonReceiverMethod.class, method);
			if (drm != null) {
				dm = DaemonScanner.scanReceiver(method, drm);
				DaemonScanner.CACHE.put(method, dm);
				return dm;
			}
		}
		return null;
	}

	private static <E extends Annotation> E isAnnotationPresent(final Class<E> annotation, final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) throws NoSuchMethodException {
		Method method = clazz.getMethod(methodName, parameterTypes);
		if (method.isAnnotationPresent(annotation)) {
			return method.getAnnotation(annotation);
		}
		for (final Class<?> interfaceClazz : clazz.getInterfaces()) {
			final E e = DaemonScanner.isAnnotationPresent(annotation, interfaceClazz, methodName, parameterTypes);
			if (e != null) {
				return e;
			}
		}
		final Class<?> superClazz = clazz.getSuperclass();
		if ((superClazz != null) && !Object.class.equals(superClazz)) {
			return DaemonScanner.isAnnotationPresent(annotation, superClazz, methodName, parameterTypes);
		}
		return null;
	}

	/**
	 * Checks also the inheritance hierarchy.
	 *
	 * @param annotation Annotation
	 * @param method Method
	 * @return Is present?
	 */
	public static boolean isAnnotationPresent(final Class<? extends Annotation> annotation, final Method method) {
		final Annotation e = DaemonScanner.getAnnotation(annotation, method);
		if (e != null) {
			return true;
		}
		return false;
	}

	/**
	 * Checks also the inheritance hierarchy.
	 *
	 * @param annotation Annotation
	 * @param method Method
	 * @param <A> Annotation
	 * @return Annotation or null
	 */
	public static <A extends Annotation> A getAnnotation(final Class<A> annotation, final Method method) {
		try {
			return DaemonScanner.isAnnotationPresent(annotation, method.getDeclaringClass(), method.getName(), method.getParameterTypes());
		} catch (final NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * @param method Method
	 */
	private static DaemonMethod scanRequest(final Method method, final DaemonRequestMethod drm) {
		if (method.getParameterTypes().length != 1) {
			throw new IllegalStateException("@DaemonRequestMethod must have one parameter");
		}
		final Type type;
		if (InterconnectObject.class.isAssignableFrom(method.getReturnType())) {
			// okay. we have an InterconnectObject return
			if (!IVO.class.isAssignableFrom(method.getReturnType())) {
				DaemonScanner.LOGGER2.warn("The method " + method + " uses InterconnectObject (which is deprecated) instead of IVO return.");
			}
			if (method.getReturnType().isInterface()) {
				DaemonScanner.LOGGER2.warn("The method " + method + " returns an interface");
			}
			type = Type.interconnectObject;
		} else if (method.getReturnType().equals(Void.TYPE)) {
			// okay. we have a void return
			type = Type.voidivo;
		} else if (InterconnectObject[].class.isAssignableFrom(method.getReturnType())) {
			if (!IVO[].class.isAssignableFrom(method.getReturnType())) {
				DaemonScanner.LOGGER2.warn("The method " + method + " uses InterconnectObject[] (which is deprecated) instead of IVO[] return.");
			}
			// okay. we have an InterconnectObject[] return
			type = Type.interconnectObjects;
		} else if (List.class.isAssignableFrom(method.getReturnType())) {
			// we have an List<?> return
			final ParameterizedType t = (ParameterizedType) method.getGenericReturnType();
			if (t.getActualTypeArguments().length != 1) {
				throw new IllegalStateException("@DaemonRequestMethod return type must be List<InterconnectObject>");
			}
			java.lang.reflect.Type innerType = t.getActualTypeArguments()[0];
			Class<?> typeClazz = null;
			if (innerType instanceof ParameterizedType) {
				typeClazz = (Class<?>) ((ParameterizedType) innerType).getRawType();
			} else {
				typeClazz = (Class<?>) innerType;
			}
			if (!InterconnectObject.class.isAssignableFrom(typeClazz)) {
				// okay. we have an List<? extends InterconnectObject> return
				throw new IllegalStateException("@DaemonRequestMethod return type must be List<InterconnectObject>");
			}
			if (!IVO.class.isAssignableFrom(typeClazz)) {
				DaemonScanner.LOGGER2.warn("The method " + method + " uses List<? extends InterconnectObject> (which is deprecated) instead of List<? extends IVO> return.");
			}
			if (typeClazz.isInterface()) {
				DaemonScanner.LOGGER2.warn("The method " + method + " returns an interface");
			}
			type = Type.interconnectObjects;
		} else {
			throw new IllegalStateException("@DaemonRequestMethod return type must be: IVO, void, List<IVO> or IVO[]");
		}
		if ((method.getExceptionTypes().length != 1) || (method.getExceptionTypes()[0] != DaemonError.class)) {
			if (method.getDeclaringClass().isInterface()) { // you can implement a method without throwing the DaemonError and that's ok in
															// the implementation as long as the interface contains the throws DaemonError
															// statement
				throw new IllegalStateException("@DaemonRequestMethod must throw exactly one exception of type DaemonError");
			}
		}
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalStateException("@DaemonRequestMethod must be public");
		}
		Class<?> paramClass = method.getParameterTypes()[0];
		if (!InterconnectObject.class.isAssignableFrom(paramClass)) {
			throw new IllegalStateException("Paramater of @DaemonRequestMethod must implement InterconnectObject");
		}
		if (!IVO.class.isAssignableFrom(paramClass)) {
			DaemonScanner.LOGGER2.warn("The method " + method + " uses InterconnectObject (which is deprecated) instead of IVO as input.");
		}
		if (paramClass.isInterface()) {
			throw new IllegalStateException("Paramater of @DaemonRequestMethod must not be an interface");
		}
		@SuppressWarnings("unchecked")
		final Class<? extends InterconnectObject> icoClazz = (Class<? extends InterconnectObject>) method.getParameterTypes()[0];
		final long timeoutInMs = drm.timeoutUnit().toMillis(drm.timeout());
		return new DaemonMethod(icoClazz, method, type, timeoutInMs, drm.secure(), drm.idempotent());
	}

	/**
	 * @param method Method
	 */
	private static DaemonMethod scanReceiver(final Method method, final DaemonReceiverMethod drm) {
		if (method.getParameterTypes().length != 1) {
			throw new IllegalStateException("@DaemonReceiverMethod must have one parameter");
		}
		if (!method.getReturnType().equals(Void.TYPE)) {
			throw new IllegalStateException("@DaemonReceiverMethod must return void");
		}
		if (method.getExceptionTypes().length > 0) {
			throw new IllegalStateException("@DaemonReceiverMethod must not throw an exception");
		}
		if (!Modifier.isPublic(method.getModifiers())) {
			throw new IllegalStateException("@DaemonReceiverMethod must be public");
		}
		Class<?> paramClass = method.getParameterTypes()[0];
		if (!InterconnectObject.class.isAssignableFrom(paramClass)) {
			throw new IllegalStateException("Paramater of @DaemonReceiverMethod must implement InterconnectObject");
		}
		if (!IVO.class.isAssignableFrom(paramClass)) {
			DaemonScanner.LOGGER2.warn("The method " + method + " uses InterconnectObject (which is deprecated) instead of IVO as input.");
		}
		if (paramClass.isInterface()) {
			throw new IllegalStateException("Paramater of @DaemonReceiverMethod must not be an interface");
		}
		@SuppressWarnings("unchecked")
		final Class<? extends InterconnectObject> icoClazz = (Class<? extends InterconnectObject>) method.getParameterTypes()[0];
		return new DaemonMethod(icoClazz, method, Type.voit, null, drm.secure(), drm.idempotent());
	}


	public interface Invoke {

		/**
		 * @param handler Handler
		 * @param method Method
		 * @param ico Request InterconnectObject
		 * @return Depends on the type
		 * @throws IllegalAccessException ...
		 * @throws IllegalArgumentException ...
		 * @throws InvocationTargetException ...
		 */
		InterconnectObject invoke(final IDaemonHandler handler, final Method method, final InterconnectObject ico) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	}


	/**
	 * We try to convert an Object to an Array and this is not easy in Java so we need a little bit of nasty magic.
	 *
	 * @param <T> Type of elements
	 * @param clazz Clazz of the Objct elements
	 * @param obj Object
	 * @return Array
	 */
	@SuppressWarnings({"unchecked", "unused"})
	public static <T> T[] object2Array(final Class<T> clazz, final Object obj) {
		return (T[]) obj;
	}


	public enum Type implements Invoke {
		/** Single InterconnectObject. */
		interconnectObject {

			@Override
			public InterconnectObject invoke(final IDaemonHandler handler, final Method method, final InterconnectObject ico) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				final Object obj = method.invoke(handler, ico);
				Preconditions.checkNotNull(obj, "return must not be null");
				return (InterconnectObject) obj;
			}
		},
		/** Multiple InterconnectObjects. */
		interconnectObjects {

			@Override
			@SuppressWarnings({"unchecked", "rawtypes"})
			public InterconnectObject invoke(final IDaemonHandler handler, final Method method, final InterconnectObject ico) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				final Object obj = method.invoke(handler, ico);
				Preconditions.checkNotNull(obj, "return must not be null");
				if (obj instanceof List) {
					return new InterconnectList((List) obj);
				} else if (obj.getClass().isArray()) {
					final List<?> list = Lists.newArrayList(DaemonScanner.object2Array(obj.getClass().getEnclosingClass(), obj));
					return new InterconnectList(list);
				}
				throw new IllegalAccessException("Invalid return value: " + obj);
			}
		},
		/** VoidIVO. */
		voidivo {

			@Override
			public InterconnectObject invoke(final IDaemonHandler handler, final Method method, final InterconnectObject ico) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				method.invoke(handler, ico);
				return new VoidIVOBuilder().build();
			}
		},
		/** void. */
		voit {

			@Override
			public InterconnectObject invoke(final IDaemonHandler handler, final Method method, final InterconnectObject ico) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				method.invoke(handler, ico);
				return null;
			}
		}
	}

	public static final class DaemonMethod {

		private final Class<? extends InterconnectObject> request;
		private final Method method;
		private final Type type;
		private final Long timeoutInMs;
		private final boolean secure;
		private final boolean idempotent;


		/**
		 * @param aRequest Request
		 * @param aMethod Method
		 * @param aType type
		 * @param aTimeoutInMs Timeout (in ms)
		 * @param aSecure Secure?
		 * @param anIdempotent Idempotent?
		 */
		public DaemonMethod(final Class<? extends InterconnectObject> aRequest, final Method aMethod, final Type aType, final Long aTimeoutInMs, final boolean aSecure, final boolean anIdempotent) {
			super();
			this.request = aRequest;
			this.method = aMethod;
			this.type = aType;
			this.timeoutInMs = aTimeoutInMs;
			this.secure = aSecure;
			this.idempotent = anIdempotent;
		}

		/**
		 * @param aRequest Request
		 * @param aMethod Method
		 * @param aType type
		 * @param aTimeoutInMs Timeout (in ms)
		 * @param aSecure Secure?
		 * @deprecated Use DaemonMethod(aRequest, aMethod, aType, aTimeoutInMs, aSecure, anIdempotent) instead
		 */
		@Deprecated
		public DaemonMethod(final Class<? extends InterconnectObject> aRequest, final Method aMethod, final Type aType, final Long aTimeoutInMs, final boolean aSecure) {
			this(aRequest, aMethod, aType, aTimeoutInMs, aSecure, false);
		}

		/**
		 * @return Request
		 */
		public Class<? extends InterconnectObject> getRequest() {
			return this.request;
		}

		/**
		 * @return Method
		 */
		public Method getMethod() {
			return this.method;
		}

		/**
		 * @return Type
		 */
		public Type getType() {
			return this.type;
		}

		/**
		 * @return Timeout (in ms) (is null for type voit)
		 */
		public Long getTimeoutInMs() {
			return this.timeoutInMs;
		}

		/**
		 * @return Secure?
		 */
		public boolean isSecure() {
			return this.secure;
		}

		/**
		 * @return Idempotent?
		 */
		public boolean isIdempotent() {
			return this.idempotent;
		}

		/**
		 * @param handler Handler
		 * @param ivo Request IVO
		 * @return Depends on the type
		 * @throws IllegalAccessException ...
		 * @throws IllegalArgumentException ...
		 * @throws InvocationTargetException ...
		 */
		public InterconnectObject invoke(IDaemonHandler handler, InterconnectObject ivo) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			return this.getType().invoke(handler, this.getMethod(), ivo);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = (prime * result) + ((this.request == null) ? 0 : this.request.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			DaemonMethod other = (DaemonMethod) obj;
			if (this.request == null) {
				if (other.request != null) {
					return false;
				}
			} else if (!this.request.equals(other.request)) {
				return false;
			}
			return true;
		}

	}
}
