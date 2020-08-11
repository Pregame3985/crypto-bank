package com.tokimi.common.chain.utils;

import com.tokimi.common.Utils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

/**
 * @author william
 */
@Slf4j
public final class BinaryUtils {

    public static final String HEX_PREFIX = "0x";

    private BinaryUtils() {
    }

    public static Long hexToLong(String hex) {

        if (Utils.isEmpty(hex)) {
            return null;
        }

        if (hex.startsWith(HEX_PREFIX)) {
            hex = hex.substring(HEX_PREFIX.length());
        }

        return Long.parseLong(hex, 16);
    }

//    public static String removePrefix(String hex) {
//
//        if (Utils.isEmpty(hex)) {
//            return null;
//        }
//
//        if (hex.startsWith(HEX_PREFIX)) {
//            return hex.substring(HEX_PREFIX.length());
//        }
//
//        return hex;
//    }

    public static String compactSizeUintToHex(BigInteger number) {
        return uint8ToHex(number);
    }

    public static String uint8ToHex(BigInteger number) {
        return toHex(number, 1);
    }

    public static String uint16ToHex(BigInteger number) {
        return toHex(number, 2);
    }

    public static String uint32ToHex(BigInteger number) {
        return toHex(number, 4);
    }

    public static String uint64ToHex(BigInteger number) {
        return toHex(number, 8);
    }

    private static String toHex(BigInteger number, int capacity) {
        byte[] numberBytes = number.toByteArray();
        ByteBuffer bb = ByteBuffer.allocate(capacity);
        for (int i = numberBytes.length - 1; i >= 0; i--) {
            bb.put(numberBytes[i]);
            if (bb.position() == capacity) {
                break;
            }
        }
        return Hex.toHexString(bb.array());
    }

    public static BigInteger hexToInt(String hex) {
        return new BigInteger(reverse(hex), 16);
    }

    private static void reverseByteArray(byte[] array) {
        for (int i = 0; i < array.length / 2; i++) {
            short temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = (byte) temp;
        }
    }

    public static String reverse(String hash) {

        byte[] hashByte = Hex.decode(hash);
        reverseByteArray(hashByte);
        return Hex.toHexString(hashByte);
    }

    public static byte[] sha256(byte[] data) {
        return hash(new BouncyCastleProvider(), "SHA-256", data);
    }

    public static byte[] ripemd160(byte[] data) {
        return hash(new BouncyCastleProvider(), "RIPEMD160", data);
    }

    public static byte[] md5(byte[] data) {
        return hash(new BouncyCastleProvider(), "MD5", data);
    }

    public static byte[] sha256Twice(byte[] raw) {
        return sha256(sha256(raw));
    }

    public static byte[] base64(byte[] data) {
        // TODO:
        return sha256(sha256(data));
    }

    private static byte[] hash(Provider provider, String algorithm, byte[] data) {

        if (null == data) {
            return null;
        }

        Security.addProvider(provider);

        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            return md.digest(data);
        } catch (NoSuchAlgorithmException e) {
            log.error("hash algorithm {} failed : {}", algorithm, e);
        }

        return null;
    }

    public static byte[] sha3256hash(byte[] raw) {

        if (null == raw) {
            return null;
        }

        Security.addProvider(new BouncyCastleProvider());

        try {
            MessageDigest md = MessageDigest.getInstance("SHA3-256", BouncyCastleProvider.PROVIDER_NAME);
            return md.digest(raw);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("sha3-256 hash failed : {}", e);
        }
        return null;
    }

    public static byte[] sha3256hash(byte[]... raws) {

        if (null == raws) {
            return null;
        }

        Security.addProvider(new BouncyCastleProvider());

        try {
            byte[] raw = Arrays.concatenate(raws);

            MessageDigest md = MessageDigest.getInstance("SHA3-256", BouncyCastleProvider.PROVIDER_NAME);
            return md.digest(raw);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("sha3-256 hash failed : {}", e);
        }
        return null;
    }

    public static byte[] keccak256hash(byte[] raw) {

        if (null == raw) {
            return null;
        }

        Security.addProvider(new BouncyCastleProvider());

        try {
            MessageDigest md = MessageDigest.getInstance("Keccak-256", BouncyCastleProvider.PROVIDER_NAME);
            return md.digest(raw);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("keccak-256 hash failed : {}", e);
        }
        return null;
    }

    public static byte[] sha256hash(byte[] raw) {

        if (null == raw) {
            return null;
        }

        Security.addProvider(new BouncyCastleProvider());

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256", BouncyCastleProvider.PROVIDER_NAME);
            byte[] hash = md.digest(raw);
            return hash;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("hash failed : {}", e);
        }
        return null;
    }
}