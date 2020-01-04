package com.mcb.address.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.Sha256Hash;

/**
 * @author william
 */
@Slf4j
public final class AddressUtils {

    public static boolean isValidAddress(String address) {

        boolean result = true;

        if (CommonUtils.isEmpty(address)) {
            return false;
        }

        byte[] decode;

        try {
            decode = Base58.decode(address);
        } catch (AddressFormatException e) {
            log.error("address format error : {} / {}", address, e.getLocalizedMessage());
            return false;
        }

        if (decode.length < 25) {
            log.info("decode address length less than 25 bytes");
            return false;
        }

        byte[] checksum = new byte[4];
        byte[] hash160 = new byte[21];
        System.arraycopy(decode, 0, hash160, 0, 21);
        System.arraycopy(decode, 21, checksum, 0, 4);

        byte[] fullChecksum = Sha256Hash.hashTwice(hash160);

        for (int i = 0; i < checksum.length; i++) {
            if (checksum[i] != fullChecksum[i]) {
                result = false;
                break;
            }
        }
        return result;
    }
}
