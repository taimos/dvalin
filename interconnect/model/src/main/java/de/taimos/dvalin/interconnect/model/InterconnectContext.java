package de.taimos.dvalin.interconnect.model;

import java.util.UUID;
import java.util.function.Supplier;

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
            return requestClass;
        }

        public void setRequestClass(Class<? extends IVO> requestClass) {
            this.requestClass = requestClass;
        }

        @Override
        public UUID uuid() {
            return uuid;
        }

        public void setUuid(UUID uuid) {
            this.uuid = uuid;
        }

        @Override
        public int deliveryCount() {
            return deliveryCount;
        }

        public void setDeliveryCount(int deliveryCount) {
            this.deliveryCount = deliveryCount;
        }

        @Override
        public boolean redelivered() {
            return redelivered;
        }

        public void setRedelivered(boolean redelivered) {
            this.redelivered = redelivered;
        }
    }

    private static final ThreadLocal<InnerContext> threadLocalContext = createLocalContext();

    private static ThreadLocal<InnerContext> createLocalContext() {
        return new ThreadLocal<>().withInitial(new Supplier<InnerContext>() {
            @Override
            public InnerContext get() {
                return new InnerContext();
            }
        });
    }

    private static InnerContext getThreadLocalContext() {
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
