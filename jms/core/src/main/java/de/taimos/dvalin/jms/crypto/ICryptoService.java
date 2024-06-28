package de.taimos.dvalin.jms.crypto;

import de.taimos.dvalin.jms.exceptions.InfrastructureException;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;

import javax.jms.JMSException;
import javax.jms.TextMessage;

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
     * @throws MessageCryptoException  on crypto errors
     * @throws InfrastructureException on infrastructure exception
     */
    boolean isMessageSecure(final TextMessage txt) throws MessageCryptoException, InfrastructureException;

    /**
     * @param txt the message to encrypt
     * @return the decrypted text message
     * @throws MessageCryptoException on crypto errors
     */
    TextMessage decryptMessage(final TextMessage txt) throws MessageCryptoException;

    /**
     * @param txt the message to encrypt
     * @return the secured text message
     * @throws JMSException           on JMS errors
     * @throws MessageCryptoException on crypto errors
     */
    TextMessage secureMessage(final TextMessage txt) throws JMSException, MessageCryptoException;
}
