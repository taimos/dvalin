package de.taimos.dvalin.jms.exceptions;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class CreationException extends InfrastructureException {

    private static final long serialVersionUID = -1604742258978348120L;

    /**
     * @param exceptionSource source of the exception
     * @param cause           for the exception
     */
    public CreationException(Source exceptionSource, Throwable cause) {
        super(exceptionSource.getMsg(), cause);
    }

    public enum Source {
        SESSION("Can not create session"), //
        CONNECTION("Can not create connection"), //
        DESTINATION("Can not create destination"), //
        REPLY_TO_DESTINATION("Can not create eply to destination"), //
        CONSUMER("Can not create consumer"), //
        PRODUCER("Can not create producer"); //

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
}
