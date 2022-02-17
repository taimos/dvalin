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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

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
    private static final String AES_DECODER = System.getProperty(InterconnectConstants.PROPERTY_CRYPTO_AESDECODER, "base64");
    private static final String GCM_MODE = System.getProperty(InterconnectConstants.PROPERTY_CRYPTO_GCM_MODE, "true");


    private static final String SIGNATURE = System.getProperty(InterconnectConstants.PROPERTY_CRYPTO_SIGNATURE);


    /**
     * @param data the data to encrypt
     * @return the encrypted BASE64 data
     * @throws CryptoException on encryption error
     */
    public static String crypt(final String data) throws CryptoException {
        if (data == null) {
            return null;
        }

        if (Boolean.TRUE.equals(Boolean.valueOf(MessageCryptoUtil.GCM_MODE))) {
            return MessageCryptoUtil.cryptGCM(data);
        }
        return MessageCryptoUtil.cryptECB(data);
    }

    private static String cryptGCM(String data) throws CryptoException {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[12]; //NEVER REUSE THIS IV WITH SAME KEY
            secureRandom.nextBytes(iv);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv);
            final Cipher cipher = MessageCryptoUtil.getCipherGCM(Cipher.ENCRYPT_MODE, parameterSpec);

            final byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encrypted.length);
            byteBuffer.put(iv);
            byteBuffer.put(encrypted);
            byte[] cipherMessage = byteBuffer.array();
            return Base64.encodeBase64String(cipherMessage);
        } catch (final Exception e) {
            throw new CryptoException("Encryption of data failed!", e);
        }
    }

    private static String cryptECB(String data) throws CryptoException {
        try {
            final Cipher cipher = MessageCryptoUtil.getCipher(Cipher.ENCRYPT_MODE);
            final byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(encrypted);
        } catch (final Exception e) {
            throw new CryptoException("Encryption of data failed!", e);
        }
    }


    /**
     * @param data the BASE 64 data
     * @return the decrypted data
     * @throws CryptoException on decryption error
     */
    public static String decrypt(final String data) throws CryptoException {
        if (data == null) {
            return null;
        }

        if (Boolean.TRUE.equals(Boolean.valueOf(GCM_MODE))) {
            return MessageCryptoUtil.decryptGCM(data);
        }
        return MessageCryptoUtil.decryptECB(data);


    }


    private static String decryptGCM(final String data) throws CryptoException {
        try {
            byte[] cipherMessage = Base64.decodeBase64(data);
            AlgorithmParameterSpec gcmIv = new GCMParameterSpec(128, cipherMessage, 0, 12);
            final Cipher cipher = MessageCryptoUtil.getCipherGCM(Cipher.DECRYPT_MODE, gcmIv);
            return new String(cipher.doFinal(cipherMessage, 12, cipherMessage.length - 12), StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new CryptoException("Decryption of data failed!", e);
        }
    }

    private static String decryptECB(String data) throws CryptoException {
        try {
            final Cipher cipher = MessageCryptoUtil.getCipher(Cipher.DECRYPT_MODE);
            return new String(cipher.doFinal(Base64.decodeBase64(data)), StandardCharsets.UTF_8);
        } catch (final Exception e) {
            throw new CryptoException("Decryption of data failed!", e);
        }
    }

    private static Cipher getCipher(int mode) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, DecoderException {
        final SecretKeySpec skeySpec = new SecretKeySpec(MessageCryptoUtil.getDecodedkey(), "AES");
        final Cipher cipher = Cipher.getInstance("AES");
        cipher.init(mode, skeySpec);
        return cipher;
    }

    private static Cipher getCipherGCM(int mode, AlgorithmParameterSpec gcmIv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, DecoderException, InvalidAlgorithmParameterException {
        final SecretKeySpec skeySpec = new SecretKeySpec(MessageCryptoUtil.getDecodedkey(), "AES");
        final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(mode, skeySpec, gcmIv);
        return cipher;
    }

    private static byte[] getDecodedkey() throws DecoderException {
        byte[] decodedkey;
        if ("base64".equals(MessageCryptoUtil.AES_DECODER)) {
            decodedkey = Base64.decodeBase64(MessageCryptoUtil.AES_KEY);
        } else {
            decodedkey = Hex.decodeHex(MessageCryptoUtil.AES_KEY.toCharArray());
        }
        return decodedkey;
    }

    /**
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
     * @param msg       the message to validate
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
                    System.out.print("Select key encoding (h=hex(default); b=base64): ");
                    final String encoding = scan.nextLine();
                    MessageCryptoUtil.generateKey(encoding);
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

    private static void generateKey(String encoding) {
        try {

            final KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom());
            final SecretKey skey = kgen.generateKey();
            String key;
            if ("b".equals(encoding)) {
                key = Base64.encodeBase64String(skey.getEncoded());
            } else {
                key = Hex.encodeHexString(skey.getEncoded());
            }
            System.out.println("Key: " + key);
        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
