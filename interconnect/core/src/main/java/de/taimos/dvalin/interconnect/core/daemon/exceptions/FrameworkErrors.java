package de.taimos.dvalin.interconnect.core.daemon.exceptions;

import de.taimos.dvalin.interconnect.model.service.ADaemonErrorNumber;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class FrameworkErrors {
    public static final DaemonErrorNumber FRAMEWORK_ERROR = new FrameworkError(-1);

    public static final DaemonErrorNumber UNEXPECTED_TYPE_ERROR = new FrameworkError(-20);

    public static final DaemonErrorNumber SEND_ERROR = new FrameworkError(-50);
    public static final DaemonErrorNumber RETRY_FAILED_ERROR = new FrameworkError(-51);
    public static final DaemonErrorNumber RECEIVE_ERROR = new FrameworkError(-52);
    public static final DaemonErrorNumber INVALID_RESPONSE_ERROR = new FrameworkError(-53);

    public static final DaemonErrorNumber CONNECT_CREATION_ERROR = new FrameworkError(-70);
    public static final DaemonErrorNumber SESSION_CREATION_ERROR = new FrameworkError(-71);
    public static final DaemonErrorNumber CONSUMER_CREATION_ERROR = new FrameworkError(-72);
    public static final DaemonErrorNumber PRODUCER_CREATION_ERROR = new FrameworkError(-73);
    public static final DaemonErrorNumber REPLY_TO_DESTINATION_CREATION_ERROR = new FrameworkError(-74);
    public static final DaemonErrorNumber DESTINATION_CREATION_ERROR = new FrameworkError(-75);
    public static final DaemonErrorNumber MESSAGE_CREATION_ERROR = new FrameworkError(-76);

    public static final DaemonErrorNumber MESSAGE_SERIALIZATION = new FrameworkError(-90);
    public static final DaemonErrorNumber MESSAGE_CRYPTO_ERROR = new FrameworkError(-91);


    private FrameworkErrors() {
        super();
    }


    private static final class FrameworkError extends ADaemonErrorNumber {
        private static final long serialVersionUID = -8496144395438169164L;

        FrameworkError(int aNumber) {
            super(aNumber, "framework");
        }
    }

    public static final class GenericError extends ADaemonErrorNumber {

        private static final long serialVersionUID = 7705178009518714330L;

        /**
         * @param aNumber error number
         * @param aDaemon daemon
         */
        public GenericError(int aNumber, String aDaemon) {
            super(aNumber, aDaemon);
        }

    }

}
