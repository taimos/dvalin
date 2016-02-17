/**
 *
 */
package de.taimos.dvalin.interconnect.model.common;

/*
 * #%L
 * Dvalin interconnect transfer data model
 * %%
 * Copyright (C) 2016 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
