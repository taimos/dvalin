package de.taimos.dvalin.jms.exceptions;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class CommunicationFailureException extends InfrastructureException {
    private static final long serialVersionUID = 5815412583044204150L;

    /**
     * @param communicationCause of the exception
     */
    public CommunicationFailureException(CommunicationCause communicationCause) {
        super(communicationCause.getMsg());
    }

    /**
     * @param communicationCause of the exception
     * @param cause     for the exception
     */
    public CommunicationFailureException(CommunicationCause communicationCause, Throwable cause) {
        super(communicationCause.getMsg(), cause);
    }

    public enum CommunicationCause {
        SEND("Error while sending messages"), //
        RECEIVE("Error while receiving messages"),
        INVALID_RESPONSE("Invalid response message received"),
        FAILED_TO_CREATE_MESSAGE("Failed to create message");
        private final String msg;

        CommunicationCause(String msg) {
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
