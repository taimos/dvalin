package de.taimos.dvalin.jms.activemq;

import de.taimos.dvalin.jms.crypto.ACryptoService;
import de.taimos.dvalin.jms.crypto.JmsMessageCryptoUtil;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
public class ActiveMqCryptoService extends ACryptoService {

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

            if (txt instanceof ActiveMQTextMessage) {
                final ActiveMQTextMessage t = (ActiveMQTextMessage) txt;
                t.setReadOnlyBody(false);
            }

            final String decryptedText = JmsMessageCryptoUtil.decrypt(txt.getText());
            txt.setText(decryptedText);
        } catch (final JMSException e) {
            throw new MessageCryptoException(e);
        }
        return txt;
    }
}
