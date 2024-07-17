package de.taimos.dvalin.jms.exceptions;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class CreationException extends InfrastructureException {

    private static final long serialVersionUID = -1604742258978348120L;

    private final Source exceptionSource;

    /**
     * @param exceptionSource source of the exception
     * @param cause           for the exception
     */
    public CreationException(Source exceptionSource, Throwable cause) {
        super(exceptionSource.getMsg(), cause);
        this.exceptionSource = exceptionSource;
    }

    /**
     * @param exceptionSource source of the exception
     */
    public CreationException(Source exceptionSource) {
        super(exceptionSource.getMsg());
        this.exceptionSource = exceptionSource;
    }

    public static class Source {
        public static final Source SESSION = new Source("Can not create session");
        public static final Source CONNECTION = new Source("Can not create connection");
        public static final Source DESTINATION = new Source("Can not create destination");
        public static final Source REPLY_TO_DESTINATION = new Source("Can not create reply to destination");
        public static final Source CONSUMER = new Source("Can not create consumer");
        public static final Source PRODUCER = new Source("Can not create producer");
        public static final Source FAILED_TO_CREATE_MESSAGE = new Source("Failed to create message");

        private final String msg;

        Source(String msg) {
            this.msg = msg;
        }

        /**
         * @return the msg
         */
        public String getMsg() {
            return this.msg;
        }
    }

    /**
     * @return the exceptionSource
     */
    public Source getExceptionSource() {
        return this.exceptionSource;
    }
}
