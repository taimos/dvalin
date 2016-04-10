/*
 * Copyright (c) 2016. Taimos GmbH http://www.taimos.de
 */

package de.taimos.dvalin.jaxrs.security;

import java.security.SecureRandom;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.RuntimeCryptoException;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class HashedPassword {

    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static final int DEFAULT_ROUNDOFFSET = 5000;
    private static final int PW_ROUNDBYTES = 2;

    private int roundOffset;
    private String hash;
    private String salt;

    public HashedPassword() {
        //
    }

    public HashedPassword(String password) {
        SHA512Digest sha512Digest = new SHA512Digest();
        this.setSalt(asHex(getRandomBytes(sha512Digest.getDigestSize())));
        this.setRoundOffset(DEFAULT_ROUNDOFFSET);
        this.setHash(hashPassword(password, this.salt, this.roundOffset));
    }

    public HashedPassword(int roundOffset, String hash, String salt) {
        this.roundOffset = roundOffset;
        this.hash = hash;
        this.salt = salt;
    }

    public int getRoundOffset() {
        return this.roundOffset;
    }

    public void setRoundOffset(int roundOffset) {
        this.roundOffset = roundOffset;
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(hash));
        this.hash = hash;
    }

    public String getSalt() {
        return this.salt;
    }

    public void setSalt(String salt) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(salt));
        this.salt = salt;
    }

    public boolean validate(String password) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password));
        Preconditions.checkState(!Strings.isNullOrEmpty(this.hash));
        Preconditions.checkState(!Strings.isNullOrEmpty(this.salt));
        return hashPassword(password, this.salt, this.roundOffset).equals(this.hash);
    }

    private static byte[] getRandomBytes(final int size) {
        final SecureRandom sr = new SecureRandom();
        final byte[] result = new byte[size];
        sr.nextBytes(result);
        return result;
    }

    private static String hashPassword(String password, String salt, int roundOffset) {

        final byte[] passwordBytes = stringToUTF8Bytes(password);
        final byte[] saltBytes = salt == null ? new byte[0] : stringToUTF8Bytes(salt);

        Digest digest = new SHA512Digest();
        int pwRounds = roundsFromPassword(digest, passwordBytes, saltBytes, PW_ROUNDBYTES);

        final int totalRounds = roundOffset + pwRounds;
        final PBEParametersGenerator generator = getGenerator(digest, passwordBytes, saltBytes, totalRounds);
        final CipherParameters cp = generator.generateDerivedMacParameters(digest.getDigestSize() * 8);
        if (cp instanceof KeyParameter) {
            KeyParameter kp = (KeyParameter) cp;
            return asHex(kp.getKey());
        }
        throw new RuntimeCryptoException("Invalid CipherParameter: " + cp);
    }

    private static String asHex(final byte[] bytes) {
        final char[] chars = new char[2 * bytes.length];
        for (int i = 0; i < bytes.length; ++i) {
            chars[2 * i] = HEX_CHARS[(bytes[i] & 0xF0) >>> 4];
            chars[(2 * i) + 1] = HEX_CHARS[bytes[i] & 0x0F];
        }
        return new String(chars);
    }

    private static byte[] stringToUTF8Bytes(final String s) {
        return PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(s.toCharArray());
    }

    private static PBEParametersGenerator getGenerator(final Digest digest, final byte[] pwBytes, final byte[] saltBytes, final int rounds) {
        final PBEParametersGenerator generator = new PKCS5S2ParametersGenerator(digest);
        generator.init(pwBytes, saltBytes, rounds);
        return generator;
    }

    private static int roundsFromPassword(final Digest digest, final byte[] pwBytes, final byte[] saltBytes, final int pwRoundBytes) {
        final PBEParametersGenerator generator = getGenerator(digest, pwBytes, saltBytes, 1);
        // limit key to 31 bits, we don't want negative numbers
        final CipherParameters cp = generator.generateDerivedMacParameters(Math.min(pwRoundBytes * 8, 31));
        if (cp instanceof KeyParameter) {
            KeyParameter kp = (KeyParameter) cp;
            // get derived key portion
            final String key = asHex(kp.getKey());
            return Integer.valueOf(key, 16).intValue();
        }
        throw new RuntimeCryptoException("Invalid CipherParameter: " + cp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        HashedPassword that = (HashedPassword) o;

        if (this.roundOffset != that.roundOffset) return false;
        if (this.hash != null ? !this.hash.equals(that.hash) : that.hash != null) return false;
        return this.salt != null ? this.salt.equals(that.salt) : that.salt == null;

    }

    @Override
    public int hashCode() {
        int result = this.roundOffset;
        result = 31 * result + (this.hash != null ? this.hash.hashCode() : 0);
        result = 31 * result + (this.salt != null ? this.salt.hashCode() : 0);
        return result;
    }
}
