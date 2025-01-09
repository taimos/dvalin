package de.taimos.dvalin.jms.exceptions;

import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class CommunicationFailureException extends InfrastructureException {
    private static final long serialVersionUID = 5815412583044204150L;

    private final CommunicationError communicationError;

    /**
     * @param communicationError of the exception
     */
    public CommunicationFailureException(CommunicationError communicationError) {
        super(communicationError.getMsg());
        this.communicationError = communicationError;
    }

    /**
     * @param communicationError of the exception
     * @param cause              for the exception
     */
    public CommunicationFailureException(CommunicationError communicationError, Throwable cause) {
        super(communicationError.getMsg(), cause);
        this.communicationError = communicationError;
    }

    /**
     * @return the communicationError
     */
    public CommunicationError getCommunicationError() {
        return this.communicationError;
    }

    public static class CommunicationError {

        public static final CommunicationError SEND = new CommunicationError("Error while sending messages");
        public static final CommunicationError RECEIVE = new CommunicationError("Error while receiving messages");
        public static final CommunicationError INVALID_RESPONSE = new CommunicationError(
            "Invalid response message received");

        private final String msg;

        CommunicationError(String msg) {
            this.msg = msg;
        }

        /**
         * @return the msg
         */
        public String getMsg() {
            return this.msg;
        }
    }
}
