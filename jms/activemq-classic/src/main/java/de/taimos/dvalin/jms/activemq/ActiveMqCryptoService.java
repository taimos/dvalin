package de.taimos.dvalin.jms.activemq;

import de.taimos.dvalin.jms.crypto.CryptoService;
import de.taimos.dvalin.jms.crypto.JmsMessageCryptoUtil;
import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Copyright 2024 Cinovo AG<br>
 * <br>
 *
 * @author fzwirn
 */
@Service
public class ActiveMqCryptoService extends CryptoService {

    @Override
    public TextMessage decryptMessage(TextMessage txt) throws MessageCryptoException {
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
