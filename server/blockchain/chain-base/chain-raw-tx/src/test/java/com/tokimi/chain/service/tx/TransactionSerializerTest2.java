package com.tokimi.chain.service.tx;

import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
public class TransactionSerializerTest2 {

//    // address : 2N48C3sLk9PH7mTJrgfvSFnjBfSWRiTtxho
//    private final HdWalletDTO williamWallet = generateWallet(152783, "2ba4c2fc17164133b2e72af302392c1a", 0);
//
//    // address : 2NDgppWCJZbapqpM366UNko5j13ohQcVmCD
//    private final HdWalletDTO hotWallet1 = generateWallet(-12306, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn", 0);
//
//    // address : 2NFemGqjyffc2n2eXcYLqUAmqeip7FEc9Mf
//    private final HdWalletDTO hotWallet2 = generateWallet(-12307, "v@9FX9#XNG73Eu#%gKVe2asfy6rU!WWA%JPvJ#Z^&BX@r3#FhFXXq4jb^8jaZkTwyGQF2e&ahqtHUpeHkYcWuCgHbKbPfZcZa5Xn", 0);
//
//    @Test
//    public void bip143ScriptTest() {
//        // P2SH-P2WPKH
//        // https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki#P2SHP2WPKH
//        String privateKey = "eb696a065ef48a2192da5b28b694f87544b30fae8327c4510137a922f32c6dcf";
//        String publicKey = "03ad1d8e89212f0b92c74d23bb710c00662ad1470198ac48c43f7d6f93a2a26873";
//
//        String expectedPubKeyHash = "79091972186c449eb1ded22b78e40d009bdf0089";
//        byte[] sha256 = BinaryUtils.sha256(Hex.decode(publicKey));
//        byte[] ripemd160 = BinaryUtils.ripemd160(sha256);
//        Assert.assertEquals(expectedPubKeyHash, Hex.toHexString(ripemd160));
//
//        String expectedRedeemScript = "001479091972186c449eb1ded22b78e40d009bdf0089";
//        byte[] redeemScript = ScriptBuilder.createRedeemScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedRedeemScript, Hex.toHexString(redeemScript));
//
//        String expectedScriptSig = "76a91479091972186c449eb1ded22b78e40d009bdf008988ac";
//        byte[] scriptSig = ScriptBuilder.createP2PKHOutputScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedScriptSig, Hex.toHexString(scriptSig));
//
//        String expectedScriptCode = "1976a91479091972186c449eb1ded22b78e40d009bdf008988ac";
//        byte[] scriptCode = ScriptBuilder.createScriptCode(scriptSig).getProgram();
//        Assert.assertEquals(expectedScriptCode, Hex.toHexString(scriptCode));
//
//        String expectedScriptPubKey = "a9144733f37cf4db86fbc2efed2500b4f4e49f31202387";
//        sha256 = BinaryUtils.sha256(redeemScript);
//        ripemd160 = BinaryUtils.ripemd160(sha256);
//        byte[] scriptPubKey = ScriptBuilder.createP2SHOutputScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedScriptPubKey, Hex.toHexString(scriptPubKey));
//
//        String preimage = "01000000b0287b4a252ac05af83d2dcef00ba313af78a3e9c329afa216eb3aa2a7b4613a18606b350cd8bf565266bc352f0caddcf01e8fa789dd8a15386327cf8cabe198db6b1b20aa0fd7b23880be2ecbd4a98130974cf4748fb66092ac4d3ceb1a5477010000001976a91479091972186c449eb1ded22b78e40d009bdf008988ac00ca9a3b00000000feffffffde984f44532e2173ca0d64314fcefe6d30da6f8cf27bafa706da61df8a226c839204000001000000";
//        String expectedSigHash = "64f3b0f4dd2bb3aa1ce8566d220cc74dda9df97d8490cc81d89d735c92e59fb6";
//        Assert.assertEquals(expectedSigHash, Hex.toHexString(BinaryUtils.sha256Twice(Hex.decode(preimage))));
//
//        String expectedSignature = "3044022047ac8e878352d3ebbde1c94ce3a10d057c24175747116f8288e5d794d12d482f0220217f36a485cae903c713331d877c1f64677e3622ad4010726870540656fe9dcb";
//        SignatureService signatureService = new BCSignatureService();
//        byte[] signature = signatureService.signature(Hex.decode(privateKey), Hex.decode(expectedSigHash));
//        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
//
//        Assert.assertTrue(signatureService.verify(Hex.decode(publicKey),
//                Hex.decode(expectedSigHash),
//                Hex.decode(expectedSignature)
//        ));
//    }
//
//    @Test
//    public void hotWallet1ScriptTest() {
//        // P2SH-P2WPKH
//        String privateKey = Hex.toHexString(hotWallet1.getRawPrivateKey());
//        String publicKey = Hex.toHexString(hotWallet1.getRawPublicKey());
//
//        String expectedPubKeyHash = "a97060f04d5c211db7934c085e43ce6c7519b0ca";
//        byte[] sha256 = BinaryUtils.sha256(Hex.decode(publicKey));
//        byte[] ripemd160 = BinaryUtils.ripemd160(sha256);
//        Assert.assertEquals(expectedPubKeyHash, Hex.toHexString(ripemd160));
//
//        String expectedRedeemScript = "0014a97060f04d5c211db7934c085e43ce6c7519b0ca";
//        byte[] redeemScript = ScriptBuilder.createRedeemScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedRedeemScript, Hex.toHexString(redeemScript));
//
//        String expectedScriptSig = "76a914a97060f04d5c211db7934c085e43ce6c7519b0ca88ac";
//        byte[] scriptSig = ScriptBuilder.createP2PKHOutputScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedScriptSig, Hex.toHexString(scriptSig));
//
//        String expectedScriptCode = "1976a914a97060f04d5c211db7934c085e43ce6c7519b0ca88ac";
//        byte[] scriptCode = ScriptBuilder.createScriptCode(scriptSig).getProgram();
//        Assert.assertEquals(expectedScriptCode, Hex.toHexString(scriptCode));
//
//        String expectedScriptPubKey = "a914e039fd910fc6faa6b91849761979a27683a9518387";
//        sha256 = BinaryUtils.sha256(redeemScript);
//        ripemd160 = BinaryUtils.ripemd160(sha256);
//        byte[] scriptPubKey = ScriptBuilder.createP2SHOutputScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedScriptPubKey, Hex.toHexString(scriptPubKey));
//
//        String preimage = "020000007c6c4a36e1cb5f3fe7e2ef19a44395335a0581b72055d417f586b36301ae06b418606b350cd8bf565266bc352f0caddcf01e8fa789dd8a15386327cf8cabe19890d73d87ededf9cd3606d0b7fa0e5338a0a9530d7d0782f3649ba6f574f2a1f1050000001976a914a97060f04d5c211db7934c085e43ce6c7519b0ca88aca025260000000000fffffffe2b479a78a6c29ca715db3bac9a7022e756f51bd5e14b409ba786b793148fe0910000000001000000";
//        String expectedSigHash = "53c4300dffd6be5d229bb31737f750f2b5ddf6ccdd2988a3807d71a6c6ed9027";
//        Assert.assertEquals(expectedSigHash, Hex.toHexString(BinaryUtils.sha256Twice(Hex.decode(preimage))));
//
//        String expectedSignature = "304502210080eac58254a992ba89bbbfdfbabb724e388e1a543b8e2cdf9a2f48f89b7593ab02201ee1af246e820ce6455516084f14089f8ca82d71144d2c9d74176632113ba3fa";
//        SignatureService signatureService = new BCSignatureService();
//        byte[] signature = signatureService.signature(Hex.decode(privateKey), Hex.decode(expectedSigHash));
//        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
//
//        Assert.assertTrue(signatureService.verify(Hex.decode(publicKey),
//                Hex.decode(expectedSigHash),
//                Hex.decode(expectedSignature)
//        ));
//    }
//
//    @Test
//    public void williamWalletScriptTest() {
//        // P2SH-P2WPKH
//        String privateKey = Hex.toHexString(williamWallet.getRawPrivateKey());
//        String publicKey = Hex.toHexString(williamWallet.getRawPublicKey());
//
//        String expectedPubKeyHash = "8827f62a458f748b85d60062c8befaa772736669";
//        byte[] sha256 = BinaryUtils.sha256(Hex.decode(publicKey));
//        byte[] ripemd160 = BinaryUtils.ripemd160(sha256);
//        Assert.assertEquals(expectedPubKeyHash, Hex.toHexString(ripemd160));
//
//        String expectedRedeemScript = "00148827f62a458f748b85d60062c8befaa772736669";
//        byte[] redeemScript = ScriptBuilder.createRedeemScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedRedeemScript, Hex.toHexString(redeemScript));
//
//        String expectedScriptSig = "76a9148827f62a458f748b85d60062c8befaa77273666988ac";
//        byte[] scriptSig = ScriptBuilder.createP2PKHOutputScript(ripemd160).getProgram();
//        Assert.assertEquals(expectedScriptSig, Hex.toHexString(scriptSig));
//
//        String expectedScriptCode = "1976a9148827f62a458f748b85d60062c8befaa77273666988ac";
//        byte[] scriptCode = ScriptBuilder.createScriptCode(scriptSig).getProgram();
//        Assert.assertEquals(expectedScriptCode, Hex.toHexString(scriptCode));
//
//        String preimage = "010000007c6c4a36e1cb5f3fe7e2ef19a44395335a0581b72055d417f586b36301ae06b43bb13029ce7b1f559ef5e747fcac439f1455a2ec7c5f09b72290795e7066504490d73d87ededf9cd3606d0b7fa0e5338a0a9530d7d0782f3649ba6f574f2a1f1050000001976a914a97060f04d5c211db7934c085e43ce6c7519b0ca88aca025260000000000ffffffffce3f4db5518a420dc9f9a519be12d9079d306518a45d0d2572e0ea939046cb590000000001000000";
//        String expectedSigHash = "c8fe7b5adbece7198aa68f93b4d166b4b75df9fbf92c1d3f23019fcdf25c3c20";
//        Assert.assertEquals(expectedSigHash, Hex.toHexString(BinaryUtils.sha256Twice(Hex.decode(preimage))));
//
//        String expectedSignature = "3045022100f33e63d2385422a2d7b3b58cf560969aa2e6cb4c807d69b0aadc64a628150cb202202eae34a87748a2c0affc74cdeea1d49bf0d5242cded59c265979dd0783a284db";
//        SignatureService signatureService = new BCSignatureService();
//        byte[] signature = signatureService.signature(Hex.decode(privateKey), Hex.decode(expectedSigHash));
//        Assert.assertEquals(expectedSignature, Hex.toHexString(signature));
//
//        Assert.assertTrue(signatureService.verify(Hex.decode(publicKey),
//                Hex.decode(expectedSigHash),
//                Hex.decode(expectedSignature)
//        ));
//    }
//
//    @Test
//    public void hotWallet1SendTestOmniToWilliamByBip143Test() {
//        // done see: https://live.blockcypher.com/btc-testnet/tx/76cde73aa06f06d4e417da12f7d5eddedbc4989f72c394fad9587c61f650d634/
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//        RawTransactionDTO.TxInDTO txIn = new RawTransactionDTO.TxInDTO();
//
//        txIn.getPreviousOutput().getHash().setHex("2c12c8403ec4ccf6a1641175bfa37fc064ddd10dd34cfb247e2d3f89ec2f0746");
//        txIn.getPreviousOutput().getIndex().setValue(BigInteger.valueOf(3));
//        txIn.getPreviousOutput().getTxOut().getAddress().setAddress("2NDgppWCJZbapqpM366UNko5j13ohQcVmCD");
//        txIn.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(2500000L));
//        txIn.getR().setBinary(hotWallet1.getRawPrivateKey());
//        txIn.getPreviousOutput().getTxOut().getU().setBinary(hotWallet1.getRawPublicKey());
//
//        rawTransaction.getTxIns().add(txIn);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.getValue().setValue(BigInteger.valueOf(2498454L));
//        txOut0.getAddress().setAddress("2NDgppWCJZbapqpM366UNko5j13ohQcVmCD");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        RawTransactionDTO.OmniTxOutDTO omniTxOut0 = new RawTransactionDTO.OmniTxOutDTO();
//        omniTxOut0.getIdentifier().setValue(BigInteger.valueOf(2));
//        omniTxOut0.getOmniValue().setValue(BigInteger.TEN);
//        omniTxOut0.getValue().setValue(BigInteger.TEN);
//        omniTxOut0.getAddress().setScriptType(OMNI);
//        rawTransaction.getTxOuts().add(omniTxOut0);
//
//        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
//        txOut1.getValue().setValue(BigInteger.valueOf(546L));
//        txOut1.getAddress().setAddress("2N48C3sLk9PH7mTJrgfvSFnjBfSWRiTtxho");
//        rawTransaction.getTxOuts().add(txOut1);
//
//        TransactionSerializer.Builder.of(rawTransaction, OMNI_ADDRESS_PARSER).serialize();
//
//        String expectedTxid = "76cde73aa06f06d4e417da12f7d5eddedbc4989f72c394fad9587c61f650d634";
//        String expectedWtxid = "6b64aafb092ae71a3bf43691736278e704afceafc5ed3d5d3ef007cbb139fe58";
//        String expectedSignedRawTx = "0200000000010146072fec893f2d7e24fb4cd30dd1dd64c07fa3bf751164a1f6ccc43e40c8122c0300000017160014a97060f04d5c211db7934c085e43ce6c7519b0cafffffffe03961f26000000000017a914e039fd910fc6faa6b91849761979a27683a95183870a00000000000000166a146f6d6e690000000000000002000000000000000a220200000000000017a9147754da9d93cf0a1f50ef16344094307c057b8ecf870247304402206ec71462ead26e87e90f3919444812b53af25b67c5cf67d7f6bf6cb3768eb46a02207ba7f36b1d992e571907b0b1e7436365d145d37cbb0049cc53b817db86e7aa4d01210321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec3000000000";
//
//        Assert.assertEquals(expectedTxid, rawTransaction.getTxId());
//        Assert.assertEquals(expectedWtxid, rawTransaction.getHash());
//        Assert.assertEquals(expectedSignedRawTx, rawTransaction.getSignedRawTx().getHex());
//    }
//
//    @Test
//    public void hotWallet1SendTestOmniToWilliamByLegacyTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//        rawTransaction.setVersion(new RawTransactionDTO.NumberElement(BigInteger.ONE, RawTransactionDTO.DataType.UINT_32));
//        rawTransaction.setSequence(new RawTransactionDTO.NumberElement("ffffffff", RawTransactionDTO.DataType.UINT_32));
//
//        RawTransactionDTO.TxInDTO txIn = new RawTransactionDTO.TxInDTO();
//
//        txIn.getPreviousOutput().getHash().setHex("66370fc5b89bc95359ded392648c1169ed1a86ab5efb337db8a84f9cf0e56b78");
//        txIn.getPreviousOutput().getIndex().setValue(BigInteger.ZERO);
//        txIn.getPreviousOutput().getTxOut().getAddress().setAddress("mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3");
//        txIn.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(99958148L));
//        txIn.getR().setBinary(hotWallet1.getRawPrivateKey());
//        txIn.getPreviousOutput().getTxOut().getU().setBinary(hotWallet1.getRawPublicKey());
//
//        rawTransaction.getTxIns().add(txIn);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.getValue().setValue(BigInteger.valueOf(99954056L));
//        txOut0.getAddress().setAddress("mvxs7gPpJh1S3RnzD8pVmx1V9Qxb1T5Hc3");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        RawTransactionDTO.OmniTxOutDTO omniTxOut0 = new RawTransactionDTO.OmniTxOutDTO();
//        omniTxOut0.getIdentifier().setValue(BigInteger.valueOf(2));
//        omniTxOut0.getOmniValue().setValue(BigInteger.valueOf(1000000000L));
//        rawTransaction.getTxOuts().add(omniTxOut0);
//
//        RawTransactionDTO.TxOutDTO txOut1 = new RawTransactionDTO.TxOutDTO();
//        txOut1.getValue().setValue(BigInteger.valueOf(546L));
//        txOut1.getAddress().setAddress("2NDgppWCJZbapqpM366UNko5j13ohQcVmCD");
//        rawTransaction.getTxOuts().add(txOut1);
//
//        TransactionSerializer.Builder.of(rawTransaction, OMNI_ADDRESS_PARSER).serialize();
//
//        String expectedSignedRawTx = "0100000001786be5f09c4fa8b87d33fb5eab861aed69118c6492d3de5953c99bb8c50f3766000000006b4830450221008ef912c77b7298e33e71df3c5b3f42713f6404827197eea6eeafb85e22cf85a70220206c306138ce88d10b4ef0f1f1d5b9eb1f67fd2d522eaa8edc8a23ca600e7bf701210321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec30ffffffff03882df505000000001976a914a97060f04d5c211db7934c085e43ce6c7519b0ca88ac0000000000000000166a146f6d6e690000000000000002000000003b9aca00220200000000000017a914e039fd910fc6faa6b91849761979a27683a951838700000000";
//        Assert.assertEquals(expectedSignedRawTx, rawTransaction.getSignedRawTx().getHex());
//    }
//
//    @Test
//    public void hotWallet1SendToWilliamMultiInputsRawTxTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//
//        RawTransactionDTO.TxInDTO txIn1 = new RawTransactionDTO.TxInDTO();
//        txIn1.getPreviousOutput().getHash().setHex("f1a1f274f5a69b64f382077d0d53a9a038530efab7d00636cdf9eded873dd790");
//        txIn1.getPreviousOutput().getIndex().setValue(new BigInteger("5"));
//        txIn1.getPreviousOutput().getTxOut().getAddress().setAddress("2NDgppWCJZbapqpM366UNko5j13ohQcVmCD");
//        txIn1.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(2500000L));
//        txIn1.getR().setBinary(hotWallet1.getRawPrivateKey());
//        txIn1.getPreviousOutput().getTxOut().getU().setBinary(hotWallet1.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn1);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.getValue().setValue(BigInteger.valueOf(2499000L));
//        txOut0.getAddress().setAddress("2N48C3sLk9PH7mTJrgfvSFnjBfSWRiTtxho");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//        walletEngine.setUsingSegwit(true);
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//
//        BCSignatureService signatureService = new BCSignatureService();
//
//        Assert.assertTrue(signatureService.verify(Hex.decode("0321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec30"),
//                Hex.decode("c8fe7b5adbece7198aa68f93b4d166b4b75df9fbf92c1d3f23019fcdf25c3c20"),
//                Hex.decode("30450221009d5e82dc952e3d84c4e7ff85bf5fc95813c58c845c3aeb0232a80ace0bb6d618022000ce9ab499bff7c5efaaaecb21e6238e0340800d6c289eb60f49e6f3065b8fc0")
//        ));
//    }
//
//    @Test
//    public void williamMultiInputGetTestRawTxTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//        rawTransaction.setVersion(new RawTransactionDTO.NumberElement(BigInteger.ONE, RawTransactionDTO.DataType.UINT_32));
//        rawTransaction.setSequence(new RawTransactionDTO.NumberElement("ffffffff", RawTransactionDTO.DataType.UINT_32));
//
//        RawTransactionDTO.TxInDTO txIn1 = new RawTransactionDTO.TxInDTO();
//        txIn1.getPreviousOutput().getHash().setHex("153a79c0c3f1cdbdffb9e6844e68ed5a6a59f4cb60970e945cc34069680528d8");
//        txIn1.getPreviousOutput().getIndex().setValue(BigInteger.ZERO);
//        txIn1.getPreviousOutput().getTxOut().getAddress().setAddress("mwvQbQ3AvzFsjntFDJPW5c9RLQV8RwNHr6");
//        txIn1.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(129989474L));
//        txIn1.getR().setBinary(hotWallet2.getRawPrivateKey());
//        txIn1.getPreviousOutput().getTxOut().getU().setBinary(hotWallet2.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn1);
//
//        RawTransactionDTO.TxInDTO txIn2 = new RawTransactionDTO.TxInDTO();
//        txIn2.getPreviousOutput().getHash().setHex("7150a36323c1a08ef9c95a4550ac367a05dbbe704b5d3939065219802571b8e3");
//        txIn2.getPreviousOutput().getIndex().setValue(BigInteger.ZERO);
//        txIn2.getPreviousOutput().getTxOut().getAddress().setAddress("mwvQbQ3AvzFsjntFDJPW5c9RLQV8RwNHr6");
//        txIn2.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(69970674L));
//        txIn2.getR().setBinary(hotWallet2.getRawPrivateKey());
//        txIn2.getPreviousOutput().getTxOut().getU().setBinary(hotWallet2.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn2);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.getValue().setValue(BigInteger.valueOf(199959148L));
//        txOut0.getAddress().setAddress("msvt4irNDojtpLY3vBvg6RQnv35nQz35Th");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//
//        String expectedSignedRawTx = "0100000002d82805686940c35c940e9760cbf4596a5aed684e84e6b9ffbdcdf1c3c0793a15000000006a4730440220157a66ea87f91b041f51f92f658db488d31cf9cb51f7eed86fb727b1fcac59080220152665a5bd2e3243d394c60a748b377babaeeee924a067124240a5986e81da8e01210330f4dd33c69e7f4f6162c4548a60c2f82de8e459fff5f2f5778f1326e7c71422ffffffffe3b871258019520639395d4b70bedb057a36ac50455ac9f98ea0c12363a35071000000006b4830450221009ec84aecf33f95ac22c38b77656c2053e01cc5083272777870153f0e59fdb92d02204dc2dbd8a04828817a5dcdb43ed5723a8e1344d87a5973362a77fafb9918e69a01210330f4dd33c69e7f4f6162c4548a60c2f82de8e459fff5f2f5778f1326e7c71422ffffffff016c22eb0b000000001976a9148827f62a458f748b85d60062c8befaa77273666988ac00000000";
//        Assert.assertEquals(expectedSignedRawTx, rawTransaction.getSignedRawTx().getHex());
//    }
//
//
//    // TODO: unfinished case txid: 4da4629cb4ddc357848683575990fdc71bbb9826098e543a298aea25ec1b8316
//
//    @Test
//    public void mixedMultiRawTxTest() {
//
//        RawTransactionDTO rawTransaction = new RawTransactionDTO();
//        rawTransaction.setVersion(new RawTransactionDTO.NumberElement(BigInteger.ONE, RawTransactionDTO.DataType.UINT_32));
//        rawTransaction.setSequence(new RawTransactionDTO.NumberElement("ffffffff", RawTransactionDTO.DataType.UINT_32));
//
//        RawTransactionDTO.TxInDTO txIn1 = new RawTransactionDTO.TxInDTO();
//        txIn1.getPreviousOutput().getHash().setHex("21d0ee9d25f8f37c6ec1b9e7abae8ab24652e88b4c3e67d041bb02ed8c65ea44");
//        txIn1.getPreviousOutput().getIndex().setValue(BigInteger.ZERO);
//        txIn1.getPreviousOutput().getTxOut().getAddress().setAddress("msvt4irNDojtpLY3vBvg6RQnv35nQz35Th");
//        txIn1.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(100000000L));
//        txIn1.getR().setBinary(williamWallet.getRawPrivateKey());
//        txIn1.getPreviousOutput().getTxOut().getU().setBinary(williamWallet.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn1);
//
//        RawTransactionDTO.TxInDTO txIn2 = new RawTransactionDTO.TxInDTO();
//        txIn2.getPreviousOutput().getHash().setHex("e9061cc6f3dc9bbdea6e79adc0fe4140b86bdcda05d528a49114adf71827b11f");
//        txIn2.getPreviousOutput().getIndex().setValue(BigInteger.ONE);
//        txIn2.getPreviousOutput().getTxOut().getAddress().setAddress("2NDgppWCJZbapqpM366UNko5j13ohQcVmCD");
//        txIn2.getPreviousOutput().getTxOut().getValue().setValue(BigInteger.valueOf(109998778L));
//        txIn2.getR().setBinary(hotWallet1.getRawPrivateKey());
//        txIn2.getPreviousOutput().getTxOut().getU().setBinary(hotWallet1.getRawPublicKey());
//        rawTransaction.getTxIns().add(txIn2);
//
//        RawTransactionDTO.TxOutDTO txOut0 = new RawTransactionDTO.TxOutDTO();
//        txOut0.getValue().setValue(BigInteger.valueOf(209997778L));
//        txOut0.getAddress().setAddress("mwvQbQ3AvzFsjntFDJPW5c9RLQV8RwNHr6");
//        rawTransaction.getTxOuts().add(txOut0);
//
//        TransactionSerializer.Builder.of(rawTransaction).serialize();
//
//        String expectedSignedRawTx = "0100000000010244ea658ced02bb41d0673e4c8be85246b28aaeabe7b9c16e7cf3f8259deed021000000006b48304502210097a0c3638ddaaabb2d702a14cf7df8459db87d149fdf6039575ed9bdb22cef7f0220591dffe6a9143f5c3aba9d17d3d23f981ca76aa0f144339fb11798e2a365ed370121020b505c1ee9f7217a07d9e429afa338bcfc6b7a34bae52a907c31ee200c0b9ef0ffffffff1fb12718f7ad1491a428d505dadc6bb84041fec0ad796eeabd9bdcf3c61c06e90100000017160014a97060f04d5c211db7934c085e43ce6c7519b0caffffffff01d24f840c000000001976a914b3f1887206f086528631d726d8255f58e53d3aff88ac00024730440220463fc126d1ea9bfebda75325d82d5115a33c90f8ac22fe6d44ee63faa6c0991e02204ad9fd3256db99cd939eec50e2b38419b7cac1359b013ca77438d80e35d4e00001210321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec3000000000";
//        Assert.assertEquals(expectedSignedRawTx, rawTransaction.getSignedRawTx().getHex());
//    }
//
//
//    private HdWalletDTO generateWallet(int userId, String salt, int index) {
//        BtcWalletEngine walletEngine = new TestBtcWalletEngine();
//
//        walletEngine.setUsingSegwit(false);
//        walletEngine.setBtcNetworkParameters(RegTestParams.get());
//
//        SeedRule seedRule = new DefaultSeedRule(userId, salt);
//        return walletEngine.generateAddress(seedRule, index);
//    }
}