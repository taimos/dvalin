package de.taimos.dvalin.jms.crypto;

import de.taimos.dvalin.jms.exceptions.MessageCryptoException;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public interface ICryptoService {

    /**
     * @param txt the message to encrypt
     * @return if message is secure
     * @throws MessageCryptoException on crypto errors
     */
    boolean isMessageSecure(final Message txt) throws MessageCryptoException;

    /**
     * @param txt the message to encrypt
     * @return the decrypted text message
     * @throws MessageCryptoException on crypto errors
     */
    Message decryptMessage(final Message txt) throws MessageCryptoException;

    /**
     * @param txt the message to encrypt
     * @return the secured text message
     * @throws JMSException           on JMS errors
     * @throws MessageCryptoException on crypto errors
     */
    Message secureMessage(final Message txt) throws JMSException, MessageCryptoException;
}
