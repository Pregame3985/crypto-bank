package com.tokimi.chain.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.tokimi.common.Utils;
import com.tokimi.common.chain.utils.AddressType;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.common.chain.utils.NetworkType;
import com.tokimi.common.chain.utils.ScriptType;

import org.bouncycastle.util.encoders.Hex;

import lombok.Getter;
import lombok.Setter;


/**
 * @author william
 * @see "https://bitcoin.org/en/developer-reference#raw-transaction-format"
 */
@Getter
@Setter
public class RawTransactionDTO {

    private static final String TX_SEQUENCE_HEX = "fffffffe";
    private boolean witnessEnable = false;
    // Bytes : 4, Data Type : uint32_t
    private Element version = new NumberElement(BigInteger.valueOf(2), DataType.UINT_32);
    // Bytes : 1, Data Type : uint8_t
    private Element marker = new NumberElement(BigInteger.ZERO, DataType.UINT_8);
    // Bytes : 1, Data Type : uint8_t
    private Element flag = new NumberElement(BigInteger.ONE, DataType.UINT_8);
    // Bytes : Varies, Data Type : compactSize uint
    // Bytes : Varies, Data Type : txIn
    private List<TxInDTO> txIns = new ArrayList<>();
    // Bytes : 4, Data Type : uint32_t
    private Element sequence = new NumberElement(TX_SEQUENCE_HEX, DataType.UINT_32);
    // Bytes : Varies, Data Type : compactSize uint
    // Bytes : Varies, Data Type : txIn
    private List<TxOutDTO> txOuts = new ArrayList<>();
    // Bytes : 4, Data Type : uint32_t
    private Element lockTime = new NumberElement(BigInteger.ZERO, DataType.UINT_32);
    private Element sigHashFlag = new NumberElement(BigInteger.ONE, DataType.UINT_8);
    private Element signedRawTx = new Element();
    private Element signedNoWitnessRawTx = new Element();
    private String txId;
    private String hash;
    private BigDecimal fee;

    public RawTransactionDTO() {
    }

    private BigInteger getWitnessSize() {

        BigInteger witnessSize = BigInteger.ZERO;

        if (isWitnessEnable()) {

            witnessSize = witnessSize.add(BigInteger.valueOf(marker.getLength())).add(BigInteger.valueOf(flag.getLength()));

            for (TxInDTO txIn : txIns) {

                witnessSize = witnessSize.add(BigInteger.ONE);

                for (Element witness : txIn.getWitnesses()) {
                    witnessSize = witnessSize.add(BigInteger.ONE).add(BigInteger.valueOf(witness.getLength()));
                }
            }
        }

        return witnessSize;
    }

    public BigInteger getWeight() {
        return getBaseSize().multiply(BigInteger.valueOf(3)).add(BigInteger.valueOf(signedRawTx.getLength()));
    }

    public BigInteger getBaseSize() {

        return BigInteger.valueOf(signedRawTx.getLength()).subtract(getWitnessSize());
    }

    public BigInteger getVSize() {
        return new BigDecimal(getWeight()).divide(new BigDecimal(4), RoundingMode.UP).toBigInteger();
    }

    public enum DataType {
        UINT_8, UINT_16, UINT_32, UINT_64, COMPACT
    }

    @Getter
    @Setter
    public static class TxInDTO {

        // Bytes : 36, Data Type : outpoint
        private PreviousOutputDTO previousOutput = new PreviousOutputDTO();

        // The number of bytes in the signature script. Maximum is 10,000 bytes.
        // Bytes : Varies, Data Type : compactSize uint
        // Bytes : Varies, Data Type : char[]
        // only save DER format without sig hash
        private Element signature = new Element();

        private Element r = new Element();

        private Element outpoint = new Element();        // for generate preimage

        private Element scriptCode;

        private Element scriptPubKey = new Element();

        private List<Element> witnesses = new ArrayList<>();

        private Element preimage = new Element();

        @Getter
        @Setter
        public static class PreviousOutputDTO {

            // Bytes : 32, Data Type : char[32]
            private Element hash = new Element();

            // Bytes : 4, Data Type : uint32_t
            private Element index = new NumberElement(BigInteger.ZERO, DataType.UINT_32);

            private TxOutDTO txOut = new TxOutDTO(true);
        }
    }

    @Getter
    @Setter
    public static class TxOutDTO {

        // Bytes : 8, Data Type : int64_t
        private Element value = new NumberElement(BigInteger.ZERO, DataType.UINT_64);

        // Bytes : 1+, Data Type : compactSize uint
        // Bytes : Varies, Data Type : char[]
//        private Element scriptSig = new Element();

        private Element scriptPubKey = new Element();

        private Element redeemScript = new Element();

        private Element scriptCode = new Element();

        private Element output = new Element();

        private Element u = new Element();

        private Address address = new Address();

        private boolean txOutAsTxInput;

        private boolean usingCustomParser;

        public TxOutDTO() {

        }

        public TxOutDTO(boolean txOutAsTxInput) {
            this.txOutAsTxInput = txOutAsTxInput;
        }

        @Getter
        @Setter
        public static class Address {

            private String address;

            private AddressType addressType;

            private NetworkType networkType;

            private ScriptType scriptType;
        }
    }

    @Getter
    @Setter
    public static class OmniTxOutDTO extends TxOutDTO {

        private static final String OMNI_MARK = "6f6d6e69";
        private Element mark = new Element(OMNI_MARK);
        private Element version = new NumberElement(BigInteger.ZERO, DataType.UINT_16);
        private Element type = new NumberElement(BigInteger.ZERO, DataType.UINT_16);
        private Element identifier = new NumberElement(BigInteger.ZERO, DataType.UINT_32);
        private Element omniValue = new NumberElement(BigInteger.ZERO, DataType.UINT_64);

        public OmniTxOutDTO() {
            this.setUsingCustomParser(true);
        }
    }

    @Setter
    public static class Element {

        protected String hex;

        protected byte[] binary;

        protected BigInteger value;

        public Element() {
        }

        public Element(String hex) {
            this.hex = hex;
        }

        public String getHex() {
            return __hex(false);
        }

        public String getHex(boolean wrapLength) {
            return __hex(wrapLength);
        }

        protected String __hex(boolean wrapLength) {
            String result = hex;

            if (Utils.isEmpty(result)) {
                if (null != binary) {
                    result = Hex.toHexString(binary);
                }
            }

            if (!Utils.isEmpty(result)) {
                if (wrapLength) {
                    result = String.join("", BinaryUtils.compactSizeUintToHex(BigInteger.valueOf(result.length() / 2)), result);
                }
            }

            return result;
        }

        public byte[] getBinary() {
            return __binary();
        }

        protected byte[] __binary() {

            if (null != binary) {
                return binary;
            } else {
                String __hex = __hex(false);
                if (!Utils.isEmpty(__hex)) {
                    return Hex.decode(__hex);
                } else {
                    return null;
                }
            }
        }

        public int getLength() {

            byte[] __binary = __binary();

            if (null != __binary) {
                return __binary.length;
            } else {
                return 0;
            }
        }

        public int getSize() {

            String __hex = __hex(false);

            if (!Utils.isEmpty(__hex)) {
                return __hex.length();
            } else {
                return 0;
            }
        }

        public BigInteger getValue() {
            return BinaryUtils.hexToInt(__hex(false));
        }
    }

    @Setter
    public static class NumberElement extends Element {

        private DataType dataType;

        public NumberElement(BigInteger value, DataType dataType) {
            this.value = value;
            this.dataType = dataType;
        }

        public NumberElement(String hex, DataType dataType) {
            this.hex = hex;
            this.dataType = dataType;
        }

        @Override
        protected String __hex(boolean wrapLength) {

            String result = super.__hex(wrapLength);

            if (Utils.isEmpty(result)) {

                switch (dataType) {
                    case UINT_8:
                        result = BinaryUtils.uint8ToHex(value);
                        break;
                    case UINT_16:
                        result = BinaryUtils.uint16ToHex(value);
                        break;
                    case UINT_32:
                        result = BinaryUtils.uint32ToHex(value);
                        break;
                    case UINT_64:
                        result = BinaryUtils.uint64ToHex(value);
                        break;
                    case COMPACT:
                        result = BinaryUtils.compactSizeUintToHex(value);
                        break;
                    default:
                        result = "";
                }
            }

            return result;
        }
    }
}

