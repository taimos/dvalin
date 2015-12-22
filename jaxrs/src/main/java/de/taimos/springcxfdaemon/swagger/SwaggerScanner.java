/**
 * 
 */
package de.taimos.springcxfdaemon.swagger;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ws.rs.Path;
import javax.ws.rs.ext.Provider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.taimos.springcxfdaemon.ServiceAnnotationClassesProvider;
import io.swagger.config.Scanner;
import io.swagger.config.ScannerFactory;

/**
 * Copyright 2015 Taimos GmbH<br>
 * <br>
 *
 * @author thoeger
 * 		
 */
@Component
public class SwaggerScanner implements Scanner {
	
	@Autowired
	private ServiceAnnotationClassesProvider annotationProvider;
	
	
	@PostConstruct
	public void init() {
		ScannerFactory.setScanner(this);
	}
	
	@Override
	public Set<Class<?>> classes() {
		Set<Class<?>> classes = new HashSet<>();
		for (Class<?> clz : this.annotationProvider.getClasses()) {
			if (!this.hasAnnotation(clz, Provider.class) && clz.isAnnotationPresent(Path.class)) {
				classes.add(clz);
			}
		}
		return classes;
	}
	
	private boolean hasAnnotation(Class<?> clz, Class<? extends Annotation> ann) {
		if (clz.isAnnotationPresent(ann)) {
			return true;
		}
		for (Class<?> iface : clz.getInterfaces()) {
			if (this.hasAnnotation(iface, ann)) {
				return true;
			}
		}
		return (clz.getSuperclass() != null) && this.hasAnnotation(clz.getSuperclass(), ann);
	}
	
	@Override
	public boolean getPrettyPrint() {
		return false;
	}
	
	@Override
	public void setPrettyPrint(boolean shouldPrettyPrint) {
		//
	}
	
}
