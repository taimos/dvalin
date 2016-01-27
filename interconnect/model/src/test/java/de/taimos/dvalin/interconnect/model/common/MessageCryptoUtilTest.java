/**
 *
 */
package de.taimos.dvalin.interconnect.model.common;

import org.junit.Assert;
import org.junit.Test;

import de.taimos.dvalin.interconnect.model.CryptoException;
import de.taimos.dvalin.interconnect.model.InterconnectConstants;
import de.taimos.dvalin.interconnect.model.MessageCryptoUtil;

public class MessageCryptoUtilTest {

	/**
	 * @throws CryptoException -
	 */
	@Test
	public void crypt() throws CryptoException {
        System.setProperty(InterconnectConstants.PROPERTY_CRYPTO_AESKEY, "4b5c6acc6cedc3093d7ad49d195af14a");
        System.setProperty(InterconnectConstants.PROPERTY_CRYPTO_SIGNATURE, "8602266778973c0edd198713985b9e56");

		// i'm curious ;)
		String data = "hallali lö lä lü li";
		String crypt = MessageCryptoUtil.crypt(data);
		String decrypt = MessageCryptoUtil.decrypt(crypt);
		Assert.assertEquals(data, decrypt);
	}
}
