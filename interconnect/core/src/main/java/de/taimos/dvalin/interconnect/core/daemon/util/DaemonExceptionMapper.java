package de.taimos.dvalin.interconnect.core.daemon.util;

import de.taimos.dvalin.interconnect.core.daemon.exceptions.FrameworkErrors;
import de.taimos.dvalin.interconnect.core.daemon.exceptions.UnexpectedTypeException;
import de.taimos.dvalin.interconnect.model.service.DaemonError;
import de.taimos.dvalin.interconnect.model.service.DaemonErrorNumber;
import de.taimos.dvalin.jms.exceptions.CommunicationFailureException;
import de.taimos.dvalin.jms.exceptions.CommunicationFailureException.CommunicationError;
import de.taimos.dvalin.jms.exceptions.CreationException;
import de.taimos.dvalin.jms.exceptions.CreationException.Source;
import de.taimos.dvalin.interconnect.core.exceptions.InfrastructureException;
import de.taimos.dvalin.interconnect.core.exceptions.MessageCryptoException;
import de.taimos.dvalin.interconnect.core.exceptions.SerializationException;
import de.taimos.dvalin.interconnect.core.exceptions.TimeoutException;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class DaemonExceptionMapper {


    /**
     * Calls {@link DaemonExceptionMapper#map(Exception)} and throws the result.
     *
     * @param e original exception
     * @throws DaemonError      with a {@link DaemonErrorNumber} corresponding to the original exception.
     * @throws TimeoutException are not mapped
     */
    public static void mapAndThrow(Exception e) throws DaemonError, TimeoutException {
        Exception exception = DaemonExceptionMapper.map(e);
        if (e instanceof DaemonError) {
            throw (DaemonError) exception;
        } else if (e instanceof TimeoutException) {
            throw (TimeoutException) exception;
        } else {
            throw new UnsupportedOperationException("Failed to map exception", e);
        }
    }

    /**
     * @param e original exception
     * @return {@link DaemonError} with a {@link DaemonErrorNumber} corresponding to the original exception. <br>
     * {@link TimeoutException} are returned without mapping them.
     */
    public static Exception map(Exception e) {
        if (e instanceof DaemonError) {
            return e;
        }
        if (e instanceof UnexpectedTypeException) {
            return new DaemonError(FrameworkErrors.UNEXPECTED_TYPE_ERROR, e);
        }
        if (e instanceof TimeoutException) {
            return e;
        }
        if (e instanceof InfrastructureException) {
            return DaemonExceptionMapper.handleInfrastructureException((InfrastructureException) e);
        }
        if (e instanceof SerializationException) {
            return DaemonExceptionMapper.handleSerializationException((SerializationException) e);
        }
        return new DaemonError(FrameworkErrors.FRAMEWORK_ERROR, e);
    }

    private static Exception handleInfrastructureException(InfrastructureException e) {
        if (e instanceof CommunicationFailureException) {
            return DaemonExceptionMapper.handleCommunicationFailureException((CommunicationFailureException) e);
        }
        if (e instanceof CreationException) {
            return DaemonExceptionMapper.handleCreationException((CreationException) e);
        }

        return new DaemonError(FrameworkErrors.FRAMEWORK_ERROR, e);
    }

    private static Exception handleCommunicationFailureException(CommunicationFailureException e) {
        if (CommunicationError.SEND.equals(e.getCommunicationError())) {
            return new DaemonError(FrameworkErrors.SEND_ERROR, e);
        }
        if (CommunicationError.RECEIVE.equals(e.getCommunicationError())) {
            return new DaemonError(FrameworkErrors.RECEIVE_ERROR, e);
        }
        if (CommunicationError.INVALID_RESPONSE.equals(e.getCommunicationError())) {
            return new DaemonError(FrameworkErrors.INVALID_RESPONSE_ERROR, e);
        }
        return new DaemonError(FrameworkErrors.FRAMEWORK_ERROR, e);
    }

    private static Exception handleCreationException(CreationException e) {
        if (Source.SESSION.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.SESSION_CREATION_ERROR, e);
        }
        if (Source.CONNECTION.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.CONNECT_CREATION_ERROR, e);
        }
        if (Source.DESTINATION.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.DESTINATION_CREATION_ERROR, e);
        }
        if (Source.REPLY_TO_DESTINATION.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.REPLY_TO_DESTINATION_CREATION_ERROR, e);
        }
        if (Source.CONSUMER.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.CONSUMER_CREATION_ERROR, e);
        }
        if (Source.PRODUCER.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.PRODUCER_CREATION_ERROR, e);
        }
        if (Source.FAILED_TO_CREATE_MESSAGE.equals(e.getExceptionSource())) {
            return new DaemonError(FrameworkErrors.MESSAGE_CREATION_ERROR, e);
        }
        return new DaemonError(FrameworkErrors.FRAMEWORK_ERROR, e);
    }

    private static DaemonError handleSerializationException(SerializationException e) {
        if (e instanceof MessageCryptoException) {
            return new DaemonError(FrameworkErrors.MESSAGE_CRYPTO_ERROR, e);
        }
        return new DaemonError(FrameworkErrors.MESSAGE_SERIALIZATION, e);
    }
}
