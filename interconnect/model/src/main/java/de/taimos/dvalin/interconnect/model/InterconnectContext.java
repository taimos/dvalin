package de.taimos.dvalin.interconnect.model;

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

import java.util.UUID;

import de.taimos.dvalin.interconnect.model.ivo.IVO;
import de.taimos.dvalin.interconnect.model.service.IDaemonHandler;

public class InterconnectContext {

    private static class InnerContext implements IDaemonHandler.IContext {

        private Class<? extends IVO> requestClass;
        private UUID uuid = UUID.randomUUID();
        private int deliveryCount = 1;
        private boolean redelivered;

        @Override
        public Class<? extends IVO> requestClass() {
            return this.requestClass;
        }

        public void setRequestClass(Class<? extends IVO> requestClass) {
            this.requestClass = requestClass;
        }

        @Override
        public UUID uuid() {
            return this.uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public int deliveryCount() {
            return this.deliveryCount;
        }

        public void setDeliveryCount(int deliveryCount) {
            this.deliveryCount = deliveryCount;
        }

        @Override
        public boolean redelivered() {
            return this.redelivered;
        }

        public void setRedelivered(boolean redelivered) {
            this.redelivered = redelivered;
        }
    }

    private static final ThreadLocal<InnerContext> threadLocalContext = new ThreadLocal<>();

    private static InnerContext getThreadLocalContext() {
        if (threadLocalContext.get() == null) {
            threadLocalContext.set(new InnerContext());
        }
        return threadLocalContext.get();
    }

    public static Class<? extends IVO> getRequestClass() {
        return getThreadLocalContext().requestClass();
    }

    public static UUID getUuid() {
        return getThreadLocalContext().uuid();
    }

    public static int getDeliveryCount() {
        return getThreadLocalContext().deliveryCount();
    }

    public static boolean isRedelivered() {
        return getThreadLocalContext().redelivered();
    }

    public static void setRequestClass(Class<? extends IVO> requestClass) {
        getThreadLocalContext().setRequestClass(requestClass);
    }

    public static void setUuid(UUID uuid) {
        getThreadLocalContext().setUuid(uuid);
    }

    public static void setDeliveryCount(int deliveryCount) {
        getThreadLocalContext().setDeliveryCount(deliveryCount);
    }

    public static void setRedelivered(boolean redelivered) {
        getThreadLocalContext().setRedelivered(redelivered);
    }

    public static void reset() {
        threadLocalContext.remove();
    }

    public static IDaemonHandler.IContext getContext() {
        return getThreadLocalContext();
    }
}
