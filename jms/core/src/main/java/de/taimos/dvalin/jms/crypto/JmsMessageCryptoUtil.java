package de.taimos.dvalin.jms.crypto;

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

import de.taimos.dvalin.jms.exceptions.MessageCryptoException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Scanner;

/**
 * Utility class for encryption and decryption of JMS Messages.
 *
 * @author thoeger
 */
public final class JmsMessageCryptoUtil {

    private static final Logger logger = LoggerFactory.getLogger(JmsMessageCryptoUtil.class);

    /**
     * Constant for the system property that holds the AES key for message encryption
     */
    public static final String PROPERTY_CRYPTO_AESKEY = "interconnect.crypto.aes";

    /**
     * Constant for the system property that holds the Signature key for message encryption
     */
    public static final String PROPERTY_CRYPTO_SIGNATURE = "interconnect.crypto.signature";

    private JmsMessageCryptoUtil() {
        // Utility class with private constructor
    }


    /**
     * The name of the Signature Header
     */
    public static final String SIGNATURE_HEADER = "Signature";

    // DO NEVER EVER CHANGE THIS; MESSAGES ARE NOT RECOVERABLE AFTER CHANGE
    private static final String AES_KEY = System.getProperty(JmsMessageCryptoUtil.PROPERTY_CRYPTO_AESKEY);
    private static final String SIGNATURE = System.getProperty(JmsMessageCryptoUtil.PROPERTY_CRYPTO_SIGNATURE);


    /**
     * @param data the data to encrypt
     * @return the encrypted BASE64 data
     * @throws MessageCryptoException on encryption error
     */
    public static String crypt(final String data) throws MessageCryptoException {
        if (data == null) {
            return null;
        }
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[12]; //NEVER REUSE THIS IV WITH SAME KEY
            secureRandom.nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            final Cipher cipher = JmsMessageCryptoUtil.getCipher(Cipher.ENCRYPT_MODE, parameterSpec);

            final byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encrypted.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);
            byte[] cipherMessage = byteBuffer.array();
            return Base64.encodeBase64String(cipherMessage);
        } catch (final Exception e) {
            throw new MessageCryptoException("Encryption of data failed!", e);
        }
    }

    /**
     * @param data the BASE 64 data
     * @return the decrypted data
     * @throws MessageCryptoException on decryption error
     */
    public static String decrypt(final String data) throws MessageCryptoException {
        if (data == null) {
            return null;
        }
        try {
            byte[] cipherMessage = Base64.decodeBase64(data);
            AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, 12);
            final Cipher cipher = JmsMessageCryptoUtil.getCipher(Cipher.DECRYPT_MODE, gcmIv);
            return new String(cipher.doFinal(cipherMessage, 12, cipherMessage.length - 12), StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new MessageCryptoException("Decryption of data failed!", e);
        }
    }

    private static Cipher getCipher(int mode, AlgorithmParameterSpec gcmIv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        final SecretKeySpec skeySpec = new SecretKeySpec(Base64.decodeBase64(JmsMessageCryptoUtil.AES_KEY), "AES");
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, skeySpec, gcmIv);
        return cipher;
    }

    /**
     * @param msg the message to sign
     * @return the signature hash
     * @throws MessageCryptoException on sign error
     */
    public static String sign(final String msg) throws MessageCryptoException {
        try {
            final String toEnc = msg + JmsMessageCryptoUtil.SIGNATURE;
            final MessageDigest mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(toEnc.getBytes(StandardCharsets.UTF_8), 0, toEnc.length());
            return new BigInteger(1, mdEnc.digest()).toString(16);
        } catch (final Exception e) {
            throw new MessageCryptoException("Creating signature failed", e);
        }
    }

    /**
     * @param msg       the message to validate
     * @param signature the signature hash to check
     * @return validation result
     * @throws MessageCryptoException on sign error
     */
    public static boolean validate(final String msg, final String signature) throws MessageCryptoException {
        if ((signature == null) || signature.isEmpty()) {
            return false;
        }
        final String calcSig = JmsMessageCryptoUtil.sign(msg);
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
                    JmsMessageCryptoUtil.generateKey();
                    break;
                case "c":
                    System.out.print("Input data: ");
                    final String data = scan.nextLine();
                    System.out.println(JmsMessageCryptoUtil.crypt(data));
                    break;
                case "d":
                    System.out.print("Input data: ");
                    final String ddata = scan.nextLine();
                    System.out.println(JmsMessageCryptoUtil.decrypt(ddata));
                    break;
                default:
                    break;
            }
        } catch (final Exception e) {
            JmsMessageCryptoUtil.logger.error("Unknown exception", e);
        }
    }

    private static void generateKey() {
        try {
            final KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            final SecretKey skey = kgen.generateKey();
            String key = Base64.encodeBase64String(skey.getEncoded());
            System.out.println("Key: " + key);
        } catch (final NoSuchAlgorithmException e) {
            JmsMessageCryptoUtil.logger.error("Unknown encryption algorithm", e);
        }
    }

}
