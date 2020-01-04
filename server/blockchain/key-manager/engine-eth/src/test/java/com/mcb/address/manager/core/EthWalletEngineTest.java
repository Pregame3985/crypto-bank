package com.mcb.address.manager.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.mcb.address.manager.util.ChainSettings.CHAIN_TEST_NETWORK;

/**
 * @author william
 */
public class EthWalletEngineTest {

    private WalletEngine testAddressEngine;

    @Before
    public void setUp() {

        testAddressEngine = new EthWalletEngine(CHAIN_TEST_NETWORK);
    }


    @Test
    public void isValidEthAddress() {
        String btcAddress = "2N48C3sLk9PH7mTJrgfvSFnjBfSWRiTtxho";
        Assert.assertFalse(testAddressEngine.isValid(btcAddress));
    }

}