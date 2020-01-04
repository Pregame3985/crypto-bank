package com.mcb.address.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.*;

/**
 * @author william
 */
@Slf4j
public class BinaryUtils {

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
        return org.bouncycastle.util.encoders.Hex.toHexString(bb.array());
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

        byte[] hashByte = org.bouncycastle.util.encoders.Hex.decode(hash);
        reverseByteArray(hashByte);
        return org.bouncycastle.util.encoders.Hex.toHexString(hashByte);
    }

    public static byte[] sha256(byte[] data) {
        return hash(new BouncyCastleProvider(), "SHA-256", data);
    }

    public static byte[] ripemd160(byte[] data) {
        return hash(new BouncyCastleProvider(), "RIPEMD160", data);
    }

    public static byte[] sha256Twice(byte[] raw) {
        return sha256(sha256(raw));
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

    public static byte[] ripemd160Hash(byte[] raw) {

        if (null == raw) {
            return null;
        }

        Security.addProvider(new BouncyCastleProvider());

        try {
            MessageDigest md = MessageDigest.getInstance("RIPEMD160", BouncyCastleProvider.PROVIDER_NAME);
            return md.digest(raw);
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("ripemd160 hash failed : {}", e);
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

    public static byte[] convertBits(byte[] bytes8Bits, int from, int to, boolean strictMode) {
        int length = (int) (strictMode ? Math.floor((double) bytes8Bits.length * from / to)
                : Math.ceil((double) bytes8Bits.length * from / to));
        int mask = ((1 << to) - 1) & 0xff;
        byte[] result = new byte[length];
        int index = 0;
        int accumulator = 0;
        int bits = 0;
        for (int i = 0; i < bytes8Bits.length; i++) {
            byte value = bytes8Bits[i];
            accumulator = (((accumulator & 0xff) << from) | (value & 0xff));
            bits += from;
            while (bits >= to) {
                bits -= to;
                result[index] = (byte) ((accumulator >> bits) & mask);
                ++index;
            }
        }
        if (strictMode) {
            if (bits > 0) {
                result[index] = (byte) ((accumulator << (to - bits)) & mask);
                ++index;
            }
        } else {
            if (!(bits < from && ((accumulator << (to - bits)) & mask) == 0)) {
                throw new RuntimeException("Strict mode was used but input couldn't be converted without padding");
            }
        }

        return result;
    }
}
