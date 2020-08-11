package com.tokimi.chain.service.tx;

import org.junit.Before;
import org.junit.Test;


/**
 * @author william
 */
public class TransactionDeserializerTest {

    String raw;

    byte[] binary;

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    @Before
    public void setUp() throws Exception {

        // https://bch.btc.com/7a191125f65afa42a24817d0b9f8d5300d1fa6c8a698c3c092e027972519cfc1
        raw = "0100000001263ac1e6af9a32ad93f850be998baddcea0c997a7912fa1836f91301fa595d88000000006b48304502210094e41d6176259750cc5742e38008ccdb2407b3279e8d26825397c4289798a95502202254ba52ce7952534285c67bf88073e04f5490f7e8e870ec808bf21bde860cc2012102553f81e1f1f1454ff5b1c2939a810145401301f7b45a5d205930b072bbf4d35affffffff0302871800000000001976a914fdfdb043be8fa563a8797718f86dcdf2648e1a6588ac0000000000000000166a146f6d6e69000000000000001f000000218711a00022020000000000001976a914d9acc0d071e74ce1237179462ca6a6fbfcc4913d88ac00000000";
        binary = hexStringToByteArray(raw);
    }

    @Test
    public void test() {
//        RawTransactionDTO rawTransactionDTO = TransactionDeserializer.Builder.of(binary).deserialize();
//        Assert.assertEquals(1, rawTransactionDTO.getVersion().intValue());
//        Assert.assertEquals(1, rawTransactionDTO.getTxInCount().intValue());
//        Assert.assertEquals("beb27aba4d821d01e6a8373d26409c07a07960f3360e273445be2058289ca46e", rawTransactionDTO.getTxIns().get(0).getPreviousOutput().getHash());
//        Assert.assertEquals(0, rawTransactionDTO.getTxIns().get(0).getPreviousOutput().getIndex().intValue());
//        Assert.assertEquals(107, rawTransactionDTO.getTxIns().get(0).getScriptBytes().intValue());
//        Assert.assertEquals("4830450221009cc4913c020085f56c55de490305a349cedd5bd79a521a7ab5895958b087ba5e022048d31e2c727c9ec650421337b915a548223012d495cb9b6dd9d50cc95c247c4f01210321be5c0a1c9e2003e74e4f0c378eed604aa838595c7f168e9507198e8d06ec30", rawTransactionDTO.getTxIns().get(0).getSignatureScript());
//        Assert.assertEquals(3, rawTransactionDTO.getTxOutCount().intValue());
//
//        Assert.assertEquals(2389162L, rawTransactionDTO.getTxOuts().get(0).getValue().longValue());
//        Assert.assertEquals(25, rawTransactionDTO.getTxOuts().get(0).getPkScriptBytes().intValue());
//        Assert.assertEquals("76a914a97060f04d5c211db7934c085e43ce6c7519b0ca88ac", rawTransactionDTO.getTxOuts().get(0).getPkScript());
//        Assert.assertEquals(P2PKH, rawTransactionDTO.getTxOuts().get(0).getAddress().getScriptType());
//
//        Assert.assertEquals(0L, rawTransactionDTO.getTxOuts().get(1).getValue().longValue());
//
//        Assert.assertEquals(546L, rawTransactionDTO.getTxOuts().get(2).getValue().longValue());
//        Assert.assertEquals(25, rawTransactionDTO.getTxOuts().get(2).getPkScriptBytes().intValue());
//        Assert.assertEquals("76a9141ac86b58141aba005e3a515d94fd5f0ead04d72a88ac", rawTransactionDTO.getTxOuts().get(2).getPkScript());
//        Assert.assertEquals(P2PKH, rawTransactionDTO.getTxOuts().get(2).getAddress().getScriptType());
//
//        Assert.assertEquals(0, rawTransactionDTO.getLockTime().intValue());
    }
//
//    @Test
//    public void testUtils1() {
//        String rawhex = "010000000805a1f898bfb63d20d58a5d9b053fa833c44101f8331fbc15b781eb6432f2ecb9060000006b483045022100961338b63f3921c3465b6c67185adee0b9b914723fadd48c38dee16d8bc8d0de022024ad9633297d2a49d2a6720c6e41332265accf08f8b6b6598f4c7252831754e44121023365f4592c79027aa11f3c08203e0e3fa0438798d88e1da2742ca8b3af1498faffffffffda140bf0b750b9d9e2bf33fd5835c1b59b35e8876f4daad5a6ab81bf9f27841d020000006a47304402207c0b7e07a7487801011d6a3b98a4b953df20bd485457eae15439caae5de1da5d0220222b4427b356fd381f3fbcbd8b49690b766392371a796dfc45aa0ada3c3a805a4121024e2058dc33ef2985e63b709772cf09067584e58a2eca16303ca683c97ed7fa2fffffffffac166278256ed296b1b71c591910b0b1c3be9ee7557a3567f4a54a5616582842010000006a47304402207f6fd20d7d40c80aec24b739c34ddf899c2bcb69e186f14403f2cc4174c4fbc802202dcf3a02c1945ee1129e42fe065ff31511ab5612b83643cd52c69bd4e9302abf412103feabdc5c70b2686016c86b76ad5d5e70de892602390df0ab1bf8d86195d523baffffffffd70bbaa2b7f4eb012d652655bf4d1d673d780562c01e0484a1adbb4950eec254010000006b4830450221008a67ac7c896b9a4c180bd828c18fbb82bee9fed0438b8419c7ba303520c3c34c022064a0b693478c4e88cfc6ff017b23ee0c0d3fcc6f025a6717b5d000f83f6795a641210259c676fd55878de0ad97a84d14fd44e256b3358223bd8de9cb56f9a7954edf97ffffffff35faaefe73d2dc3fe40d052b54e23fe987a9a3c9c227cff87bdbe6c14382fd2d010000006b483045022100b19e60cf29aab2fb9b878659eae3cfd67d8fe971585dfef027e56e9a6c78164302203405dbd7994249bb1da3ae7557d865c96d435b113cf64f1447ab1778c1176d34412102fd54f28b12ca566dcd89e04b6f23500fe568a2d11196fa74f78de728f317ebf0ffffffffc07ede705a8fe9557b7234165643298639519d3a3fdd1f2e56bc412e188df6ad020000006a47304402205e3ba85271db4591a419b9e4d7b99c04efdaa56dfce463b5be71631fb6a2910102203d0671de0beaa318caae942502ab47e6d130da97403a13ce4a666ac403b14bb4412103022d5215d83c2d7f8d8884ff9b88842350d51e62941a9a518aa4b1d3e0c02dd2ffffffff8dba38755cbc5981acd97698e4eacced39f7855f1c6d050553f12dde474edf20010000006b483045022100a35f4fcdd6c0745c026afe05741063e1574c0607d42e5ab48534b472f21cb62f02201cdb3de4dfbd8b7ecdaa8c38182f1ad93f3a9827dcdda0f09efe36003a089b1641210371d24d3b73048297624c5bf226b1f61d309054d7f3cd02d5f9b8f3182721afcaffffffff9e13737c8a33031fc5290dd72a6151f43be8a025a9f9729ee0092ceafdc02880010000006a47304402200ad05effa0c231e3e5a225efef023d8cc631d1ea1730e7f2e588a2fcf6585b9a022043e40a48048f52b0ead4b869138cf12cbf17f06bf01c16c936fda153345450ac4121032f6af2aded7b919a0638371c37fe1fc5aa5176c21ad8d8eace60b9a9f40fa53effffffff032018362b020000001976a914f6259225de0e952c8af4cfb0a284df3493a5d7a888ac10af1300000000001976a914b2a83c1f9dbb6bed3c1cb60f0903113af816fb0c88acacbabf2c000000001976a914e2f3d92ad24cc1eb37e736bc4a47067eab65b9f488ac00000000";
//        RawTransactionDTO rawTransactionDTO = TransactionDeserializer.Builder.of(hexStringToByteArray(rawhex)).deserialize();
//    }
//
//    @Test
//    public void testUtils2() {
//        String rawhex = "02000000012ac85b5e34b5b94e27179407b8f5ae0a60067c83f5b98b6e597395d50050bef3010000006a473044022043805cf00038f5cd61f70f358d49011c83c994c93379c95c26d230bf3ce714b20220772101569a300aeb0e02525a9f59f68ef421550e4948e081812b41b1151cdd21412103c7388992adb32d54af6de7eb885be4a1908b61fe6d45120b3a5a21dbfd8d52f3ffffffff02f0a29a3b0000000017a9140c63ce64bc8fe486ef69be06ed790cc1b181ce25874a523101000000001976a914aa72f9d6d8c08f631af0e4ff4a6f05cc5eb5a06788ac00000000";
//        RawTransactionDTO rawTransactionDTO = TransactionDeserializer.Builder.of(hexStringToByteArray(rawhex)).deserialize();
//    }
}