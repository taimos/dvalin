package de.taimos.dvalin.interconnect.model;

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

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public final class MessageCryptoUtil {

	private MessageCryptoUtil() {
		// Utility class with private constructor
	}


	/**
	 * The name of the Signature Header
	 */
	public static final String SIGNATURE_HEADER = "Signature";

	// DO NEVER EVER CHANGE THIS; MESSAGES ARE NOT RECOVERABLE AFTER CHANGE
	private static final String AES_KEY = System.getProperty(InterconnectConstants.PROPERTY_CRYPTO_AESKEY);
	private static final String SIGNATURE = System.getProperty(InterconnectConstants.PROPERTY_CRYPTO_SIGNATURE);


	/**
	 *
	 * @param data the data to encrypt
	 * @return the encrypted BASE64 data
	 * @throws CryptoException on encryption error
	 */
	public static String crypt(final String data) throws CryptoException {
		if (data == null) {
			return null;
		}

		try {
			final Cipher cipher = MessageCryptoUtil.getCipher(Cipher.ENCRYPT_MODE);
			final byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			return Base64.encodeBase64String(encrypted);
		} catch (final Exception e) {
			throw new CryptoException("Encryption of data failed!", e);
		}
	}

	/**
	 *
	 * @param data the BASE 64 data
	 * @return the decrypted data
	 * @throws CryptoException on decryption error
	 */
	public static String decrypt(final String data) throws CryptoException {
		if (data == null) {
			return null;
		}

		try {
			final Cipher cipher = MessageCryptoUtil.getCipher(Cipher.DECRYPT_MODE);
			return new String(cipher.doFinal(Base64.decodeBase64(data)), StandardCharsets.UTF_8);
		} catch (final Exception e) {
			throw new CryptoException("Decryption of data failed!", e);
		}
	}

	private static Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		final SecretKeySpec skeySpec = new SecretKeySpec(Base64.decodeBase64(MessageCryptoUtil.AES_KEY), "AES");
		final Cipher cipher = Cipher.getInstance("AES");
		cipher.init(mode, skeySpec);
		return cipher;
	}

	/**
	 *
	 * @param msg the message to sign
	 * @return the signature hash
	 * @throws CryptoException on sign error
	 */
	public static String sign(final String msg) throws CryptoException {
		try {
			final String toEnc = msg + MessageCryptoUtil.SIGNATURE;
			final MessageDigest mdEnc = MessageDigest.getInstance("MD5");
			mdEnc.update(toEnc.getBytes(StandardCharsets.UTF_8), 0, toEnc.length());
			return new BigInteger(1, mdEnc.digest()).toString(16);
		} catch (final Exception e) {
			throw new CryptoException("Creating signature failed", e);
		}
	}

	/**
	 *
	 * @param msg the message to validate
	 * @param signature the signature hash to check
	 * @return validation result
	 * @throws CryptoException on sign error
	 */
	public static boolean validate(final String msg, final String signature) throws CryptoException {
		if ((signature == null) || signature.isEmpty()) {
			return false;
		}
		final String calcSig = MessageCryptoUtil.sign(msg);
		return calcSig.equals(signature);
	}

	// ##################################################################
	// Helper methods to generate keys and (de/en)crypt data
	// ##################################################################

	/**
	 * start this to generate an AES key or (de/en)crypt data
	 *
	 * @param args the CLI arguments
	 */
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		try {
			System.out.println("Select (k=generate key; c=crypt; d=decrypt):");
			System.out.println();
			Scanner scan = new Scanner(System.in, "UTF-8");
			if (!scan.hasNextLine()) {
				return;
			}
			switch (scan.nextLine()) {
			case "k":
				MessageCryptoUtil.generateKey();
				break;
			case "c":
				System.out.print("Input data: ");
				final String data = scan.nextLine();
				System.out.println(MessageCryptoUtil.crypt(data));
				break;
			case "d":
				System.out.print("Input data: ");
				final String ddata = scan.nextLine();
				System.out.println(MessageCryptoUtil.decrypt(ddata));
				break;
			default:
				break;
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private static void generateKey() {
		try {
			final KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(256, new SecureRandom());
			final SecretKey skey = kgen.generateKey();
			System.out.println("Key: " + Base64.encodeBase64String(skey.getEncoded()));
		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
