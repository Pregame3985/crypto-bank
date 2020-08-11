package com.tokimi.chain.service.tx;

import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
public class TransactionSerializerTest {

//    private RawTransactionDTO rawTransaction = new RawTransactionDTO();
//    private static String curveName = "secp256k1";
//
//    private X9ECParameters params;
//    private ECParameterSpec paramSpec;
//    private SeedRule hotWalletSeedRule;
//    private SeedRule userSeedRule;
//    private ECDomainParameters domainParams;
//
//    // address : msvt4irNDojtpLY3vBvg6RQnv35nQz35Th
//    private final HdWalletDTO williamWallet = generateWallet(152783, "2ba4c2fc17164133b2e72af302392c1a", 0);
//
//    // address : mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3
//    private final HdWalletDTO hotWallet1 = generateWallet(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn", 0);
//
//    // address : mwvQbQ3AvzFsjntFDJPW5c9RLQV8RwNHr6
//    private final HdWalletDTO hotWallet2 = generateWallet(-12307, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn", 0);
//
//    @Before
//    public void setUp() {
//
//        Security.addProvider(new BouncyCastleProvider());
//
//        this.params = SECNamedCurves.getByName(curveName);
//        this.domainParams = new ECDomainParameters(params.getCurve(),
//                params.getG(), params.getN(), params.getH());
//        this.paramSpec = new ECParameterSpec(params.getCurve(), params.getG(), params.getN(), params.getH());
//
//        // address : mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3
//        hotWalletSeedRule = new DefaultSeedRule(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn");
//        // address : msvt4irNDojtpLY3vBvg6RQnv35nQz35Th / 2N48C3sLk9PH7mTJrgfvSFnjBfSWRiTtxho
//        userSeedRule = new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a");
//
//        RawTransactionDTO.TxInDTO txIn = new RawTransactionDTO.TxInDTO();
//
//        txIn.getPreviousOutput().setHash("ee079eb3132359bdc9333834500bd652c3972d3eae4a3db7a91e42232fd82e82");
//        txIn.getPreviousOutput().setIndex(BigInteger.ZERO);
//        txIn.getPreviousOutput().getTxOut().getAddress().setAddress("mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3");
//        txIn.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(130000000L));
//        HdWalletDTO wallet = generateWallet(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn", 0);
//        txIn.setR(wallet.getRawPrivateKey());
//        txIn.setU(wallet.getRawPublicKey());
////        txIn.getPreviousOutput().getTxOut().getAddress().setPubKeyHashHex("8827f62a458f748b85d60062c8befaa772736669");
//
//        rawTransaction.getTxIns().add(txIn);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//
//        txOut0.setValue(BigDecimal.valueOf(129980000L));
//        txOut0.getAddress().setAddress("msvt4irNDojtpLY3vBvg6RQnv35nQz35Th");
//
//        rawTransaction.getTxOuts().add(txOut0);
//
////        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
////        txOut1.setValue(BigInteger.valueOf(9999000L));
////        txOut1.getAddress().setAddress("mpQw1AmkQ3Z76AXPcmudngYAcSpwPooyyz");
////
////        rawTransaction.getTxOuts().add(txOut1);
//    }
//
//    @Test
//    public void williamRawTxTest() {
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//    }
//
//    @Test
//    public void hotWalletRawTxTest() {
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//    }
//
//    @Test
//    public void williamMultiInputGetTestRawTxTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//
//        RawTransactionDTO.TxInDTO txIn1 = new RawTransactionDTO.TxInDTO();
//        txIn1.getPreviousOutput().setHash("153a79c0c3f1cdbdffb9e6844e68ed5a6a59f4cb60970e945cc34069680528d8");
//        txIn1.getPreviousOutput().setIndex(BigInteger.ZERO);
//        txIn1.getPreviousOutput().getTxOut().getAddress().setAddress("mwvQbQ3AvzFsjntFDJPW5c9RLQV8RwNHr6");
//        txIn1.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(129989474L));
//        txIn1.setR(hotWallet2.getRawPrivateKey());
//        txIn1.setU(hotWallet2.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn1);
//
//        RawTransactionDTO.TxInDTO txIn2 = new RawTransactionDTO.TxInDTO();
//        txIn2.getPreviousOutput().setHash("7150a36323c1a08ef9c95a4550ac367a05dbbe704b5d3939065219802571b8e3");
//        txIn2.getPreviousOutput().setIndex(BigInteger.ZERO);
//        txIn2.getPreviousOutput().getTxOut().getAddress().setAddress("mwvQbQ3AvzFsjntFDJPW5c9RLQV8RwNHr6");
//        txIn2.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(69970674L));
//        txIn2.setR(hotWallet2.getRawPrivateKey());
//        txIn2.setU(hotWallet2.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn2);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.setValue(BigDecimal.valueOf(199959148L));
//        txOut0.getAddress().setAddress("msvt4irNDojtpLY3vBvg6RQnv35nQz35Th");
//        rawTransaction.getTxOuts().add(txOut0);
//
////        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
////        txOut1.setValue(BigDecimal.valueOf(69980000L));
////        txOut1.getAddress().setAddress("msvt4irNDojtpLY3vBvg6RQnv35nQz35Th");
////        rawTransaction.getTxOuts().add(txOut1);
//
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//    }
//
//    @Test
//    public void williamGetTestOmniRawTxTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//
//        RawTransactionDTO.TxInDTO txIn = new RawTransactionDTO.TxInDTO();
//
//        txIn.getPreviousOutput().setHash("4774d6c9afd066790a4b10676f0efcec84a36ecd9d786ed33ee3fa4e88415f71");
//        txIn.getPreviousOutput().setIndex(BigInteger.ZERO);
//        txIn.getPreviousOutput().getTxOut().getAddress().setAddress("msvt4irNDojtpLY3vBvg6RQnv35nQz35Th");
//        txIn.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(129980000L));
//        HdWalletDTO wallet = generateWallet(152783, "2ba4c2fc17164133b2e72af302392c1a", 0);
//        txIn.setR(wallet.getRawPrivateKey());
//        txIn.setU(wallet.getRawPublicKey());
//
//        rawTransaction.getTxIns().add(txIn);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.setValue(BigDecimal.valueOf(50000000L));
//        txOut0.getAddress().setAddress("moneyqMan7uh8FqdCA2BV5yZ8qVrc9ikLP");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
//        txOut1.setValue(BigDecimal.valueOf(69980000L));
//        txOut1.getAddress().setAddress("mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3");
//        rawTransaction.getTxOuts().add(txOut1);
//
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//    }
//
//    @Test
//    public void williamSendTestOmniRawTxTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//
//        RawTransactionDTO.TxInDTO txIn = new RawTransactionDTO.TxInDTO();
//
//        txIn.getPreviousOutput().setHash("66370fc5b89bc95359ded392648c1169ed1a86ab5efb337db8a84f9cf0e56b78");
//        txIn.getPreviousOutput().setIndex(BigInteger.ZERO);
//        txIn.getPreviousOutput().getTxOut().getAddress().setAddress("mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3");
//        txIn.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(99958148L));
//        txIn.setR(hotWallet1.getRawPrivateKey());
//        txIn.setU(hotWallet1.getRawPublicKey());
//
//        rawTransaction.getTxIns().add(txIn);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.setValue(BigDecimal.valueOf(99954056L));
//        txOut0.getAddress().setAddress("mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        RawTransactionDTO.OmniTxOutDTO omniTxOut0 = new RawTransactionDTO.OmniTxOutDTO();
//        omniTxOut0.setVersion(0);
//        omniTxOut0.setType(0);
//        omniTxOut0.setIdentifier(2);
//        omniTxOut0.setValue(new BigDecimal(1000000000L));
//        omniTxOut0.getAddress().setScriptType(OMNI);
//        rawTransaction.getTxOuts().add(omniTxOut0);
//
//        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
//        txOut1.setValue(BigDecimal.valueOf(546L));
//        txOut1.getAddress().setAddress("2NDgppWCJZbapqpM366UNko5j13ohQcVmCD");
//        rawTransaction.getTxOuts().add(txOut1);
//
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//
//        String expectedSignedRawTx = "0100000001786be5f09c4fa8b87d33fb5eab861aed69118c6492d3de5953c99bb8c50f3766000000006b4830450221008ef912c77b7298e33e71df3c5b3f42713f6404827197eea6eeafb85e22cf85a70220206c306138ce88d10b4ef0f1f1d5b9eb1f67fd2d522eaa8edc8a23ca600e7bf701210321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec30ffffffff03882df505000000001976a914a97060f04d5c211db7934c085e43ce6c7519b0ca88ac0000000000000000166a146f6d6e690000000000000002000000003b9aca00220200000000000017a914e039fd910fc6faa6b91849761979a27683a951838700000000";
//        Assert.assertEquals(expectedSignedRawTx, rawTransaction.getSignedRawTx());
//    }
//
//    private HdWalletDTO generateWallet(int userId, String salt, int index) {
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//
//        SeedRule seedRule = new DefaultSeedRule(userId, salt);
//        HdWalletDTO wallet = walletEngine.generateAddress(seedRule, index);
//
//        return wallet;
//    }
//
//    private KeyPair generateKeyPair(HdWalletDTO wallet) {
//        KeyPair keyPair = new KeyPair(myPublicKey(new BigInteger(1, wallet.getRawPrivateKey())),
//                myPrivateKey(new BigInteger(1, wallet.getRawPrivateKey())));
//
//        return keyPair;
//    }
//
//    private PrivateKey myPrivateKey(BigInteger s) {
//        try {
//            return KeyFactory.getInstance("ECDSA", PROVIDER_NAME).generatePrivate(new ECPrivateKeySpec(s, this.paramSpec));
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
//            return null;
//        }
//    }
//
//    private PublicKey myPublicKey(BigInteger s) {
//        ECPoint point = this.domainParams.getG().multiply(s);
//        try {
//            return KeyFactory.getInstance("ECDSA", PROVIDER_NAME).generatePublic(new ECPublicKeySpec(point, this.paramSpec));
//        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
//            return null;
//        }
//    }
//
//    // done with https://www.blocktrail.com/tBCC/tx/e150ec59ccf61c36e658c2e2ca6b429b69424c5f26025638ec497f9158ea4a69
//    @Test
//    public void multiInputAndOutTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//        rawTransaction.setSigHashFlag((byte) (rawTransaction.getSigHashFlag() | 0x40));
//
//        HdWalletDTO wallet;
//        RawTransactionDTO.TxInDTO txIn1 = new RawTransactionDTO.TxInDTO();
//
//        txIn1.getPreviousOutput().setHash("9ffbac41a23b27c25c001cabe59715e5b5790c6a5e4b939f3d8789e75e118281");
//        txIn1.getPreviousOutput().setIndex(BigInteger.ZERO);
//        txIn1.getPreviousOutput().getTxOut().getAddress().setAddress("bchtest:qpsetrh6c0kju98x57fhqfpup699q82y0vs62uh230");
//        txIn1.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(100000000L));
//        wallet = generateWallet(152744, "5d0b6960fe5e4bf99ad9a9ac27707ef2", 0);
//        txIn1.setR(wallet.getRawPrivateKey());
//        txIn1.setU(wallet.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn1);
//
//        RawTransactionDTO.TxInDTO txIn2 = new RawTransactionDTO.TxInDTO();
//        txIn2.getPreviousOutput().setHash("9ffbac41a23b27c25c001cabe59715e5b5790c6a5e4b939f3d8789e75e118281");
//        txIn2.getPreviousOutput().setIndex(BigInteger.ONE);
//        txIn2.getPreviousOutput().getTxOut().getAddress().setAddress("bchtest:qz8yknzhamqect3rz5f6zjr0hvq7qq45lq3te52njc");
//        txIn2.getPreviousOutput().getTxOut().setValue(BigDecimal.valueOf(9774452L));
//        wallet = generateWallet(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn", 0);
//        txIn2.setR(wallet.getRawPrivateKey());
//        txIn2.setU(wallet.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn2);
//
//        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
//        txOut1.setValue(BigDecimal.valueOf(100000000L));
//        txOut1.getAddress().setAddress("bchtest:qzrjrp9mpllwjc8lx6j6acg5n07kfz23pvvae6g5w2");
//
//        rawTransaction.getTxOuts().add(txOut1);
//
//        RawTransactionDTO.TxOutDTO txOut2 = new RawTransactionDTO.TxOutDTO();
//        txOut2.setValue(BigDecimal.valueOf(773452L));
//        txOut2.getAddress().setAddress("mz2c9cmGidto8noHp5sovg4pWbF1Eo6Hhn");
//        rawTransaction.getTxOuts().add(txOut2);
//
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//    }
//
//    @Test
//    public void hash256doubleTest() throws Exception {
//
//        String raw = "03c9e279105173a292ed349e2438b7b8a98bd2e104f83c2a59f664c3dad3cb7e36";
//        String expectedHash = "ecfe36ead42bd5ebd6ae136a9f592d1114686e5008bd7de8174f1c47670cc1d5";
//
//        MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] hash = digest.digest(Hex.decode(raw));
//        hash = digest.digest(hash);
//        Assert.assertEquals(expectedHash, Hex.toHexString(hash));
//
//        String raw2 = "0100000001be66e10da854e7aea9338c1f91cd489768d1d6d7189f586d7a3613f2a24d5396000000001976a914dd6cce9f255a8cc17bda8ba0373df8e861cb866e88acffffffff0123ce0100000000001976a9142bc89c2702e0e618db7d59eb5ce2f0f147b4075488ac0000000001000000";
//        String expectedHash2 = "30f10a6468b7d98257af63fb40dfcf2cefe991346fd37c67cf7b51ff8d4404d3";
//
//        digest = MessageDigest.getInstance("SHA-256");
//        hash = digest.digest(Hex.decode(raw2));
//        hash = digest.digest(hash);
//        Assert.assertEquals(expectedHash2, Hex.toHexString(hash));
//
//        String raw3 = "0100000001be66e10da854e7aea9338c1f91cd489768d1d6d7189f586d7a3613f2a24d5396000000001976a914dd6cce9f255a8cc17bda8ba0373df8e861cb866e88acffffffff0123ce0100000000001976a914a2fd2e039a86dbcf0e1a664729e09e8007f8951088ac0000000001000000";
//        String expectedHash3 = "6f48882e380e945143b7a0befaf6d47326ecc2ab043100a8cc1757b53902de1c";
//
//        digest = MessageDigest.getInstance("SHA-256");
//        hash = digest.digest(Hex.decode(raw3));
//        hash = digest.digest(hash);
//        Assert.assertEquals(expectedHash3, Hex.toHexString(hash));
//    }
//
//    // check example: https://klmoney.wordpress.com/bitcoin-dissecting-transactions-part-2-building-a-transaction-by-hand/
//    @Test
//    public void signatureTest() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
//        String raw = "0100000001416e9b4555180aaa0c417067a46607bc58c96f0131b2f41f7d0fb665eab03a7e000000001976a91499b1ebcfc11a13df5161aba8160460fe1601d54188acffffffff01204e0000000000001976a914e81d742e2c3c7acd4c29de090fc2c4d4120b2bf888ac0000000001000000";
//        String expectedHash = "456f9e1b6184d770f1a240da9a3c4458e55b6b4ba2244dd21404db30b3131b94";
//
//        MessageDigest digest = MessageDigest.getInstance("SHA-256", BouncyCastleProvider.PROVIDER_NAME);
//        byte[] hash = digest.digest(Hex.decode(raw));
//        hash = digest.digest(hash);
//        Assert.assertEquals(expectedHash, Hex.toHexString(hash));
//
//        String privateKeyHex = "ee0de6ed77712741e5c995f5cb7779a8a838b65ffcdf9f4a87ad31a16fc9197e";
//        String publicKeyHex = "020b505c1ee9f7217a07d9e429afa338bcfc6b7a34bae52a907c31ee200c0b9ef0";
//        Signature sigGenerator = Signature.getInstance("NONEwithECDSA", BouncyCastleProvider.PROVIDER_NAME);
//
//        PrivateKey privateKey = myPrivateKey(new BigInteger(1, Hex.decode(privateKeyHex)));
//        log.debug("private key : {}", Hex.toHexString(privateKey.getEncoded()));
//        sigGenerator.initSign(privateKey);
//        sigGenerator.update(hash);
//        byte[] signature = sigGenerator.sign();
//
//        log.info("signature : {}", Hex.toHexString(signature));
//
//        PublicKey publicKey = myPublicKey(new BigInteger(1, Hex.decode(privateKeyHex)));
//
//        KeyPair keyPair = new KeyPair(publicKey, privateKey);
//        log.debug("public key : {}", Hex.toHexString(publicKey.getEncoded()));
//        Signature sigVerifier = Signature.getInstance("NONEwithECDSA", BouncyCastleProvider.PROVIDER_NAME);
//        sigVerifier.initVerify(publicKey);
//        sigVerifier.update(hash);
//        Assert.assertTrue(sigVerifier.verify(signature));
//
//        ECDSASignature ecdsaSignature = sign(new BigInteger(1, Hex.decode(privateKeyHex)), hash, curveName);
//        Assert.assertTrue(verify(new BigInteger(1, Hex.decode(privateKeyHex)), hash, ecdsaSignature, curveName));
//
//        TestBtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//
//        SignatureService signatureService = new StandardSignatureService(
//                walletEngine, new DefaultSeedRule(152783, "2ba4c2fc17164133b2e72af302392c1a"), 0);
//
//        byte[] signature1 = signatureService.signature(Hex.decode(privateKeyHex), hash);
//        Assert.assertTrue(signatureService.verify(Hex.decode(publicKeyHex), hash, signature));
//
//        sigVerifier.initVerify(publicKey);
//        sigVerifier.update(hash);
//        Assert.assertTrue(sigVerifier.verify(signature1));
//    }
//
////    private static PrivateKey getPrivateKey(BigInteger s, String curveName) {
////        try {
////            return KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME).generatePrivate(new ECPrivateKeySpec(s, paramSpec));
////        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
////            e.printStackTrace();
////            return null;
////        }
////    }
////
////    private static PublicKey getPublicKey(BigInteger s, String curveName) {
////
////        ECDomainParameters domainParams = new ECDomainParameters(params.getCurve(),
////                params.getG(), params.getN(), params.getH());
////        ECPoint point = domainParams.getG().multiply(s);
////
////        try {
////            return KeyFactory.getInstance("ECDSA", BouncyCastleProvider.PROVIDER_NAME).generatePublic(new ECPublicKeySpec(point, paramSpec));
////        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
////            e.printStackTrace();
////            return null;
////        }
////    }
//
//    public ECDSASignature sign(BigInteger s, byte[] message, String curveName) {
//        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
//        X9ECParameters ecCurve = SECNamedCurves.getByName(curveName);
//        ECDomainParameters domainParams = new ECDomainParameters(ecCurve.getCurve(),
//                ecCurve.getG(), ecCurve.getN(), ecCurve.getH(),
//                ecCurve.getSeed());
//        ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(s, domainParams);
//        signer.init(true, privKey);
//        BigInteger[] components = signer.generateSignature(message);
//        return new ECDSASignature(ecCurve, components[0], components[1]).toCanonicalised();
//    }
//
//    public static boolean verify(BigInteger s, byte[] message, ECDSASignature signature, String curveName) {
//        ECDSASigner signer = new ECDSASigner();
//        X9ECParameters ecCurve = SECNamedCurves.getByName(curveName);
//
//        ECDomainParameters domainParams = new ECDomainParameters(ecCurve.getCurve(),
//                ecCurve.getG(), ecCurve.getN(), ecCurve.getH(),
//                ecCurve.getSeed());
//
//        ECPoint point = domainParams.getG().multiply(s);
//
//        ECPublicKeyParameters params = new ECPublicKeyParameters(point, domainParams);
//        signer.init(false, params);
//        try {
//            return signer.verifySignature(message, signature.r, signature.s);
//        } catch (NullPointerException e) {
//            log.error("Caught NPE inside bouncy castle", e);
//            return false;
//        }
//    }
//
//    class ECDSASignature {
//        final BigInteger r, s;
//        X9ECParameters ecCurve;
//        BigInteger HALF_CURVE_ORDER;
//
//        /**
//         * Constructs a signature with the given components. Does NOT automatically canonicalise the signature.
//         */
//        public ECDSASignature(X9ECParameters ecCurve, BigInteger r, BigInteger s) {
//            this.ecCurve = ecCurve;
//            HALF_CURVE_ORDER = ecCurve.getN().shiftRight(1);
//            this.r = r;
//            this.s = s;
//        }
//
//        public boolean isCanonical() {
//            return s.compareTo(HALF_CURVE_ORDER) <= 0;
//        }
//
//        public ECDSASignature toCanonicalised() {
//            if (!isCanonical()) {
//                // The order of the curve is the number of valid points that exist on that curve. If S is in the upper
//                // half of the number of valid points, then bring it back to the lower half. Otherwise, imagine that
//                //    N = 10
//                //    s = 8, so (-8 % 10 == 2) thus both (r, 8) and (r, 2) are valid solutions.
//                //    10 - 8 == 2, giving us always the latter solution, which is canonical.
//                return new ECDSASignature(this.ecCurve, r, ecCurve.getN().subtract(s));
//            } else {
//                return this;
//            }
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (o == null || getClass() != o.getClass()) return false;
//            ECDSASignature other = (ECDSASignature) o;
//            return r.equals(other.r) && s.equals(other.s);
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hashCode(r, s);
//        }
//    }
}
