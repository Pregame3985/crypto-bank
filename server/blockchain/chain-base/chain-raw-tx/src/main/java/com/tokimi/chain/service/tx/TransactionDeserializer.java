package com.tokimi.chain.service.tx;

/**
 * @author william
 */
public final class TransactionDeserializer {

//    private ByteBuffer bb;
//    private int length;
//
//    private TransactionDeserializer(String rawString) {
//        this(rawString.getBytes());
//    }
//
//    private TransactionDeserializer(byte[] binary) {
//
//        this.bb = ByteBuffer.wrap(binary);
//        this.length = binary.length;
//    }
//
//    public RawTransactionDTO deserialize() {
//        RawTransactionDTO rawTransactionDTO = new RawTransactionDTO();
//
////        rawTransactionDTO.setLength(BigInteger.valueOf(this.length));
////
////        log.debug("tx length : {}", rawTransactionDTO.getLength());
////        deserializeVersion(rawTransactionDTO);
////        deserializeInputs(rawTransactionDTO);
////        deserializeOutputs(rawTransactionDTO);
////        deserializeLockTime(rawTransactionDTO);
//
//        return rawTransactionDTO;
//    }
//
//    private void deserializeVersion(RawTransactionDTO rawTransactionDTO) {
////        rawTransactionDTO.setVersion(readByteToUint(this.bb, 4));
//
//        log.debug("tx version : {}", rawTransactionDTO.getVersion());
//    }
//
//    public static byte[] readByte(ByteBuffer bb, int length) {
//        byte[] bytes = new byte[length];
//        bb.get(bytes, 0, length);
//        return bytes;
//    }
//
//    // uint32_t
//    public static BigInteger readByteToUint(ByteBuffer bb, int length) {
//        return toInt(readByte(bb, length));
//    }
//
//    // uint32_t
//    public static BigInteger readByteToLong(ByteBuffer bb, int length) {
//        return toLong(readByte(bb, length));
//    }
//
//    // uint32_t
//    public static int toInt(byte bt) {
//        return ByteBuffer.allocate(1).put(bt).getInt();
//    }
//
//    // uint32_t
//    public static BigInteger toInt(byte[] bytes) {
//        return BigInteger.valueOf(ByteBuffer.wrap(bytes).order(LITTLE_ENDIAN).getInt());
//    }
//
//    // uint32_t
//    public static BigInteger toLong(byte[] bytes) {
//        return BigInteger.valueOf(ByteBuffer.wrap(bytes).order(LITTLE_ENDIAN).getLong());
//    }
//
//    public static byte[] merge(byte[] bytes1, byte[] bytes2) {
//        byte[] bytes = new byte[bytes1.length + bytes2.length];
//        System.arraycopy(bytes1, 0, bytes, 0, bytes1.length);
//        System.arraycopy(bytes2, 0, bytes, bytes1.length, bytes2.length);
//        return bytes;
//    }
//
//    public static BigInteger readByteToCompactSizeUint(ByteBuffer bb) {
//        byte flag = bb.get();
//
//        if (flag == 0xfd) {
//            return new BigInteger(readByte(bb, 2));
//        } else if (flag == 0xfe) {
//            return new BigInteger(readByte(bb, 4));
//        } else if (flag == 0xff) {
//            return new BigInteger(readByte(bb, 8));
//        } else {
//            if (flag >= 0 && flag <= 252) {
//                return BigInteger.valueOf(flag);
//            } else {
//                return BigInteger.ZERO;
//            }
//        }
//    }
//
//    // TODO: refactor
//    public static byte[] toArray(int value) {
//        ByteBuffer buffer = ByteBuffer.allocate(4);
//        buffer.putInt(value);
//        buffer.flip();
//        return buffer.array();
//    }
//
//    private void deserializeInputs(RawTransactionDTO rawTransactionDTO) {
//        log.debug("================ Input Start ================");
//        deserializeInputCount(rawTransactionDTO);
//        deserializeInput(rawTransactionDTO);
//        log.debug("================ Input End   ================");
//    }
//
//    private void deserializeOutputs(RawTransactionDTO rawTransactionDTO) {
//        log.debug("================ Output Start ================");
//        deserializeOutputCount(rawTransactionDTO);
//        deserializeOutput(rawTransactionDTO);
//        log.debug("================ Output End   ================");
//    }
//
//    private void deserializeLockTime(RawTransactionDTO rawTransactionDTO) {
//        rawTransactionDTO.setLockTime(readByteToUint(this.bb, 4));
//    }
//
//    private void deserializeInputCount(RawTransactionDTO rawTransactionDTO) {
//        rawTransactionDTO.setTxInCount(readByteToCompactSizeUint(this.bb));
//        log.debug("input number : {}", rawTransactionDTO.getTxInCount());
//    }
//
//    private void deserializeOutputCount(RawTransactionDTO rawTransactionDTO) {
//        rawTransactionDTO.setTxOutCount(readByteToCompactSizeUint(this.bb));
//        log.debug("output number : {}", rawTransactionDTO.getTxOutCount());
//    }
//
//    private void deserializeInput(RawTransactionDTO rawTransactionDTO) {
//
//        for (int i = 0; i < rawTransactionDTO.getTxInCount().intValue(); i++) {
//            RawTransactionDTO.TxInDTO inputDTO = new RawTransactionDTO.TxInDTO();
//            byte[] bytes = readByte(this.bb, 32);
//            reverseByteArray(bytes);
//            inputDTO.getPreviousOutput().setHash(bytesToHex(bytes));
//            inputDTO.getPreviousOutput().setIndex(toInt(readByte(this.bb, 4)));
//            inputDTO.setScriptBytes(readByteToCompactSizeUint(this.bb));
//            inputDTO.setSignatureScript(bytesToHex(readByte(bb, inputDTO.getScriptBytes().intValue())));
//            bytes = readByte(this.bb, 4);
//            rawTransactionDTO.getTxIns().add(inputDTO);
//
//            log.debug("index : {}, prev txid : {}, index : {}, script length : {}, hash : {}",
//                    i, inputDTO.getPreviousOutput().getHash(), inputDTO.getPreviousOutput().getIndex(), inputDTO.getScriptBytes().longValue(), inputDTO.getSignatureScript());
//        }
//    }
//
//    private void deserializeOutput(RawTransactionDTO rawTransactionDTO) {
//
//        for (int i = 0; i < rawTransactionDTO.getTxOutCount().intValue(); i++) {
//            RawTransactionDTO.TxOutDTO outputDTO = new RawTransactionDTO.TxOutDTO();
//            byte[] bytes = readByte(this.bb, 8);
//            BigInteger value = toLong(bytes);
////            BigDecimal amount = BigDecimal.valueOf(value).divide(BigDecimal.TEN.pow(8), 8, RoundingMode.DOWN);
//            outputDTO.setValue(new BigDecimal(value));
//            outputDTO.setPkScriptBytes(readByteToCompactSizeUint(this.bb));
//            outputDTO.setPkScript(bytesToHex(readByte(this.bb, outputDTO.getPkScriptBytes().intValue())));
//
//            String opFlag = outputDTO.getPkScript().substring(0, 2);
//            if (opFlag.equals("76")) {
//                outputDTO.getAddress().setScriptType(P2PKH);
//            } else if (opFlag.equals("a9")) {
//                outputDTO.getAddress().setScriptType(P2SH);
//            } else if (opFlag.equals("6a")) {
//                outputDTO.getAddress().setScriptType(OMNI);
//            } else {
//                outputDTO.getAddress().setScriptType(UNKNOWN);
//            }
//
//            rawTransactionDTO.getTxOuts().add(outputDTO);
//
//            log.debug("index : {}, amount : {},  script length : {}, hash : {}, address type : {}",
//                    i, outputDTO.getValue(), outputDTO.getPkScriptBytes().intValue(), outputDTO.getPkScript(), outputDTO.getAddress().getScriptType().getName());
//        }
//    }
//
//    public static void reverseByteArray(byte[] array) {
//        for (int i = 0; i < array.length / 2; i++) {
//            int temp = array[i];
//            array[i] = array[array.length - i - 1];
//            array[array.length - i - 1] = (byte) temp;
//        }
//    }
//
//    private final static char[] hexArray = "0123456789abcdef".toCharArray();
//
//    public static String bytesToHex(byte[] bytes) {
//        char[] hexChars = new char[bytes.length * 2];
//        for (int j = 0; j < bytes.length; j++) {
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 2] = hexArray[v >>> 4];
//            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//        }
//        return new String(hexChars);
//    }
//
//
//    void deserize(byte[] raw) {
//    }
//
//    public static final class Builder {
//
//        private Builder() {
//        }
//
//        public static TransactionDeserializer of(byte[] binary) {
//            return new TransactionDeserializer(binary);
//        }
//
//        public static TransactionDeserializer of(String rawString) {
//            return new TransactionDeserializer(rawString);
//        }
//    }
}
