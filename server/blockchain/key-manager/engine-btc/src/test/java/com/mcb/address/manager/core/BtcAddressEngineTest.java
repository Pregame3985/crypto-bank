package com.mcb.address.manager.core;

import com.mcb.address.manager.model.HdWalletDTO;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static com.mcb.address.manager.util.ChainSettings.CHAIN_MAIN_NETWORK;
import static com.mcb.address.manager.util.ChainSettings.CHAIN_TEST_NETWORK;

/**
 * @author william
 */
@Slf4j
public class BtcAddressEngineTest {

    private WalletEngine testWalletEngine;
    private WalletEngine walletEngine;

    @Before
    public void setUp() {

        testWalletEngine = new BtcWalletEngine(CHAIN_TEST_NETWORK);
        walletEngine = new BtcWalletEngine(CHAIN_MAIN_NETWORK);
    }

    @Test
    public void userAddressTest() {

        String expectedPrivateKeyHex = "ee0de6ed77712741e5c995f5cb7779a8a838b65ffcdf9f4a87ad31a16fc9197e";
        String expectedPublicKeyHex = "020b505c1ee9f7217a07d9e429afa338bcfc6b7a34bae52a907c31ee200c0b9ef0";
        String expectedPublicKeyHash = "8827f62a458f748b85d60062c8befaa772736669";
        String expectedAddress = "2N48C3sLk9PH7mTJrgfvSFnjBfSWRiTtxho";

        HdWalletDTO walletDTO = testWalletEngine.generateAddress(new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a"), 0, null);

        Assert.assertEquals(expectedPrivateKeyHex, Utils.HEX.encode(walletDTO.getRawPrivateKey()));
        Assert.assertEquals(expectedPublicKeyHex, Utils.HEX.encode(walletDTO.getRawPublicKey()));
        Assert.assertEquals(expectedPublicKeyHash, Utils.HEX.encode(walletDTO.getPublicKeyHash()));
        Assert.assertEquals(expectedAddress, walletDTO.getAddress());


        HdWalletDTO prodWalletDTO = walletEngine.generateAddress(new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a"), 0, null);
        log.info("private key: {}, addr: {}", Utils.HEX.encode(prodWalletDTO.getRawPrivateKey()), prodWalletDTO.getAddress());
    }

    @Test
    public void hotWalletAddressTest() {

        String expectedPrivateKeyHex = "8810064c48f8c45dec6920f2d5bc72aa41dafcbaf747875e2fe6b654d7bf4000";
        String expectedPublicKeyHex = "0321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec30";
        String expectedPublicKeyHash = "a97060f04d5c211db7934c085e43ce6c7519b0ca";
        String expectedAddress = "2NDgppWCJZbapqpM366UNko5j13ohQcVmCD";

        HdWalletDTO walletDTO = testWalletEngine.generateAddress(new DefaultSeedRule(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn"), 0, null);

        Assert.assertEquals(expectedPrivateKeyHex, Utils.HEX.encode(walletDTO.getRawPrivateKey()));
        Assert.assertEquals(expectedPublicKeyHex, Utils.HEX.encode(walletDTO.getRawPublicKey()));
        Assert.assertEquals(expectedPublicKeyHash, Utils.HEX.encode(walletDTO.getPublicKeyHash()));
        Assert.assertEquals(expectedAddress, walletDTO.getAddress());
    }

    @Test
    public void isValidAddress() {

        String address1 = "msvt4irNDojtpLY3vBvg6RQnv35nQz35Th";
        String address2 = "mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3";
        String address3 = "1KuDNgeQUqJDeuwAiu6M3yLUiKyw68Dypr";
        String address4 = "1DJFUdnZ84JTA7aq7n9JaUwvUGjRxBJFnb";
        String address5 = "2MuaKKVHLhqQMKusDrFEor9kxEKiwMsMJDC";
        String address6 = "n3daXRpwfMZKYHWPQuKcKX11JGyHYHETDk";
        String address7 = "33daXRpwfMZKYHWPQuKcKX11JGyHYHETDk";
        String ltcAddress = "LZgyAHFek2RSriinaPapTP6478kJyCSRXP";

        Assert.assertTrue(testWalletEngine.isValid(address1));
        Assert.assertTrue(testWalletEngine.isValid(address2));
        Assert.assertFalse(testWalletEngine.isValid(address3));
        Assert.assertFalse(testWalletEngine.isValid(address4));
        Assert.assertTrue(testWalletEngine.isValid(address5));
        Assert.assertTrue(testWalletEngine.isValid(address6));
        Assert.assertFalse(testWalletEngine.isValid(address7));
        Assert.assertFalse(testWalletEngine.isValid(ltcAddress));

        Assert.assertFalse(walletEngine.isValid(address1));
        Assert.assertFalse(walletEngine.isValid(address2));
        Assert.assertTrue(walletEngine.isValid(address3));
        Assert.assertTrue(walletEngine.isValid(address4));
        Assert.assertFalse(walletEngine.isValid(address5));
        Assert.assertFalse(walletEngine.isValid(address6));
        Assert.assertFalse(walletEngine.isValid(address7));
        Assert.assertFalse(walletEngine.isValid(ltcAddress));
    }
}