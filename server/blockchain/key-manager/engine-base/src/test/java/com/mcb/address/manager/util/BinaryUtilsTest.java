package com.mcb.address.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Test;

/**
 * @author william
 */
@Slf4j
public class BinaryUtilsTest {

    @Test
    public void sha3256hash() {

        byte[] hash = BinaryUtils.sha3256hash("039d3fd42039f6f9b74a7efeea706f31349bb1cc5f72109e0251e174b23a92bc2a".getBytes());

        log.info(Hex.toHexString(hash));
    }
}