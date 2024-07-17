package de.taimos.dvalin.jms.crypto;

import de.taimos.dvalin.jms.exceptions.MessageCryptoException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public abstract class ACryptoService implements ICryptoService {


    @Override
    public boolean isMessageSecure(final Message txt) throws MessageCryptoException {
        try {
            return txt.propertyExists(JmsMessageCryptoUtil.SIGNATURE_HEADER);
        } catch (JMSException e) {
            throw new MessageCryptoException(e);
        }
    }

    @Override
    public Message secureMessage(final Message msg) throws JMSException, MessageCryptoException {
        if (!(msg instanceof TextMessage)) {
            return msg;
        }
        TextMessage txt = (TextMessage) msg;
        final String cryptedText = JmsMessageCryptoUtil.crypt(txt.getText());
        txt.setText(cryptedText);
        txt.setStringProperty(JmsMessageCryptoUtil.SIGNATURE_HEADER, JmsMessageCryptoUtil.sign(cryptedText));
        return txt;
    }

    @Override
    public Message decryptMessage(Message msg) throws MessageCryptoException {
        if (!(msg instanceof TextMessage)) {
            return msg;
        }
        TextMessage txt = (TextMessage) msg;
        try {
            if (!txt.propertyExists(JmsMessageCryptoUtil.SIGNATURE_HEADER)) {
                throw new MessageCryptoException();
            }
            final String signature = txt.getStringProperty(JmsMessageCryptoUtil.SIGNATURE_HEADER);
            final boolean validate = JmsMessageCryptoUtil.validate(txt.getText(), signature);
            if (!validate) {
                throw new MessageCryptoException();
            }

            final String decryptedText = JmsMessageCryptoUtil.decrypt(txt.getText());
            txt.setText(decryptedText);
        } catch (final JMSException e) {
            throw new MessageCryptoException(e);
        }
        return txt;
    }
}
