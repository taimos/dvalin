package de.taimos.dvalin.jms.crypto;

import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
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
public class CryptoService implements ICryptoService {


    @Override
    public boolean isMessageSecure(final TextMessage txt) throws MessageCryptoException {
        try {
            return txt.propertyExists(JmsMessageCryptoUtil.SIGNATURE_HEADER);
        } catch (JMSException e) {
            throw new MessageCryptoException(e);
        }
    }

    @Override
    public TextMessage secureMessage(final TextMessage txt) throws JMSException, MessageCryptoException {
        final String cryptedText = JmsMessageCryptoUtil.crypt(txt.getText());
        txt.setText(cryptedText);
        txt.setStringProperty(JmsMessageCryptoUtil.SIGNATURE_HEADER, JmsMessageCryptoUtil.sign(cryptedText));
        return txt;
    }

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

            final String decryptedText = JmsMessageCryptoUtil.decrypt(txt.getText());
            txt.setText(decryptedText);
        } catch (final JMSException e) {
            throw new MessageCryptoException(e);
        }
        return txt;
    }
}
