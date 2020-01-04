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
public class BchWalletEngineTest extends AddressEngineTest {

    private WalletEngine testWalletEngine;
    private WalletEngine walletEngine;

    @Before
    public void setUp() {

        testWalletEngine = new BchWalletEngine(CHAIN_TEST_NETWORK);
        walletEngine = new BchWalletEngine(CHAIN_MAIN_NETWORK);
    }

    @Test
    public void userAddressTest() {

        String expectedPrivateKeyHex = "66142446db3a315951f73b5d1a100182d31512259ecc2cf29f6c1cd82fdb7f6a";
        String expectedPublicKeyHex = "03c9e279105173a292ed349e2438b7b8a98bd2e104f83c2a59f664c3dad3cb7e36";
        String expectedPublicKeyHash = "872184bb0ffee960ff36a5aee1149bfd6489510b";
        String expectedTestLegacyAddress = "msqTfrbWFDceqiSJHjzCw3zUG3JaaS9S2u";

        HdWalletDTO testWalletDTO = testWalletEngine.generateAddress(new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a"), 0, null);

        Assert.assertEquals(expectedPrivateKeyHex, Utils.HEX.encode(testWalletDTO.getRawPrivateKey()));
        Assert.assertEquals(expectedPublicKeyHex, Utils.HEX.encode(testWalletDTO.getRawPublicKey()));
        Assert.assertEquals(expectedPublicKeyHash, Utils.HEX.encode(testWalletDTO.getPublicKeyHash()));
        Assert.assertEquals(expectedTestLegacyAddress, testWalletDTO.getAddress());

        String expectedLegacyAddress = "1DKWNoWXSCBQ4bxgaB1q78n9Q3hshHpyCv";

        HdWalletDTO walletDTO = walletEngine.generateAddress(new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a"), 0, null);

        Assert.assertEquals(expectedPrivateKeyHex, Utils.HEX.encode(walletDTO.getRawPrivateKey()));
        Assert.assertEquals(expectedPublicKeyHex, Utils.HEX.encode(walletDTO.getRawPublicKey()));
        Assert.assertEquals(expectedPublicKeyHash, Utils.HEX.encode(walletDTO.getPublicKeyHash()));
        Assert.assertEquals(expectedLegacyAddress, walletDTO.getAddress());
    }

    @Test
    public void hotWalletAddressTest() {

        String expectedPrivateKeyHex = "3d7a69bd893d4946bb6bbe03fc5464786aea3b6ed71c97e53e81179d1c06d423";
        String expectedPublicKeyHex = "029243e10967a00fb18eb9d2e7a73416fa0f713fc7e3954488bc8edb8c90b6eb21";
        String expectedPublicKeyHash = "8e4b4c57eec19c2e231513a1486fbb01e002b4f8";
        String expectedCashAddress = "bchtest:qz8yknzhamqect3rz5f6zjr0hvq7qq45lq3te52njc";
        String expectedLegacyAddress = "mtVLT762h3m4vKBzVRomwAVXbeePQ4S2Sz";

        HdWalletDTO walletDTO = testWalletEngine.generateAddress(new DefaultSeedRule(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn"), 0, null);

        Assert.assertEquals(expectedPrivateKeyHex, Utils.HEX.encode(walletDTO.getRawPrivateKey()));
        Assert.assertEquals(expectedPublicKeyHex, Utils.HEX.encode(walletDTO.getRawPublicKey()));
        Assert.assertEquals(expectedPublicKeyHash, Utils.HEX.encode(walletDTO.getPublicKeyHash()));
        Assert.assertEquals(expectedLegacyAddress, walletDTO.getAddress());
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
        String address8 = "bchtest:qzrjrp9mpllwjc8lx6j6acg5n07kfz23pvvae6g5w2";
        String address9 = "qz8yknzhamqect3rz5f6zjr0hvq7qq45lq3te52njc";
        String address10 = "bitcoincash:qpcq0jlzgnnlcyfqtmqmymhw7xdaffvucvg62s4mlf";
        String address11 = "qq953kjfhlu8shfhyr9s96l23te5xm44hcysa7sug3";
        String address12 = "prqs2erwhw4cc4wwnhedans8c5m0cvj7rcjfkqhml6";
        String address13 = "bitcoincash:pq36ql2lt6v0w5v0drj6j4706rl4srlaeuzv3eau08";
        String ltcAddress = "LZgyAHFek2RSriinaPapTP6478kJyCSRXP";

        Assert.assertTrue(testWalletEngine.isValid(address1));
        Assert.assertTrue(testWalletEngine.isValid(address2));
        Assert.assertFalse(testWalletEngine.isValid(address3));
        Assert.assertFalse(testWalletEngine.isValid(address4));
        Assert.assertTrue(testWalletEngine.isValid(address5));
        Assert.assertTrue(testWalletEngine.isValid(address6));
        Assert.assertFalse(testWalletEngine.isValid(address7));
        Assert.assertTrue(testWalletEngine.isValid(address8));
        Assert.assertTrue(testWalletEngine.isValid(address9));
        Assert.assertFalse(testWalletEngine.isValid(address10));
        Assert.assertFalse(testWalletEngine.isValid(address11));
        Assert.assertFalse(testWalletEngine.isValid(address12));
        Assert.assertFalse(testWalletEngine.isValid(address13));
        Assert.assertFalse(testWalletEngine.isValid(ltcAddress));

        Assert.assertFalse(walletEngine.isValid(address1));
        Assert.assertFalse(walletEngine.isValid(address2));
        Assert.assertTrue(walletEngine.isValid(address3));
        Assert.assertTrue(walletEngine.isValid(address4));
        Assert.assertFalse(walletEngine.isValid(address5));
        Assert.assertFalse(walletEngine.isValid(address6));
        Assert.assertFalse(walletEngine.isValid(address7));
        Assert.assertFalse(walletEngine.isValid(address8));
        Assert.assertFalse(walletEngine.isValid(address9));
        Assert.assertTrue(walletEngine.isValid(address10));
        Assert.assertTrue(walletEngine.isValid(address11));
        Assert.assertTrue(walletEngine.isValid(address12));
        Assert.assertTrue(walletEngine.isValid(address13));
        Assert.assertFalse(walletEngine.isValid(ltcAddress));
    }
}