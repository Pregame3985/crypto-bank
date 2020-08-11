package com.tokimi.chain.service.tx;

import static java.math.BigInteger.valueOf;
import static org.bouncycastle.util.encoders.Hex.decode;
import static org.bouncycastle.util.encoders.Hex.toHexString;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tokimi.chain.model.RawTransactionDTO;
import com.tokimi.common.chain.utils.AddressType;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.common.chain.utils.NetworkType;
import com.tokimi.common.chain.utils.ScriptType;
import com.tokimi.common.Utils;
import com.tokimi.common.signature.service.BCSignatureService;
import com.tokimi.common.signature.service.SignatureService;

import org.bitcoinj.core.Base58;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
public class TransactionSerializer implements Serializer {

    private final static AddressParser BTC_ADDRESS_PARSER = (txOut) -> {

        RawTransactionDTO.TxOutDTO.Address address = txOut.getAddress();
        String addr = txOut.getAddress().getAddress();

        address.setAddress(addr);

        if (addr.startsWith("1")) {
            address.setScriptType(ScriptType.P2PKH);
            address.setAddressType(AddressType.BASE58);
            address.setNetworkType(NetworkType.MAIN);
        }

        if (addr.startsWith("m") || addr.startsWith("n")) {
            address.setScriptType(ScriptType.P2PKH);
            address.setAddressType(AddressType.BASE58);
            address.setNetworkType(NetworkType.TEST);
        }

        if (addr.startsWith("3")) {
            if (txOut.isTxOutAsTxInput()) {
                address.setScriptType(ScriptType.P2SHP2WPKH);
            } else {
                address.setScriptType(ScriptType.P2SH);
            }
            address.setAddressType(AddressType.BASE58);
            address.setNetworkType(NetworkType.MAIN);
        }

        if (addr.startsWith("2")) {
            if (txOut.isTxOutAsTxInput()) {
                address.setScriptType(ScriptType.P2SHP2WPKH);
            } else {
                address.setScriptType(ScriptType.P2SH);
            }
            address.setAddressType(AddressType.BASE58);
            address.setNetworkType(NetworkType.TEST);
        }

        if (address.getAddressType().equals(AddressType.BASE58)) {
            byte[] decoded = Base58.decode(addr);
            byte[] hashByte = new byte[20];
            System.arraycopy(decoded, 1, hashByte, 0, 20);

            byte[] scriptPubKey = new byte[0];
            byte[] redeemScript = new byte[0];
            byte[] scriptCode = new byte[0];

            if (address.getScriptType().equals(ScriptType.P2PKH)) {
                scriptPubKey = ScriptBuilder.createP2PKHOutputScript(hashByte).getProgram();
            } else if (address.getScriptType().equals(ScriptType.P2SH)) {
                scriptPubKey = ScriptBuilder.createP2SHOutputScript(hashByte).getProgram();
            } else if (address.getScriptType().equals(ScriptType.P2SHP2WPKH)) {
                scriptPubKey = ScriptBuilder.createP2SHOutputScript(hashByte).getProgram();

                if (null != txOut.getU().getBinary()) {
                    byte[] sha256 = BinaryUtils.sha256(txOut.getU().getBinary());
                    byte[] ripemd160 = BinaryUtils.ripemd160(sha256);
                    redeemScript = ScriptBuilder.createRedeemScript(ripemd160).getProgram();
                    byte[] scriptSig = ScriptBuilder.createP2PKHOutputScript(ripemd160).getProgram();
                    scriptCode = ScriptBuilder.createScriptCode(scriptSig).getProgram();
                }
            }
            txOut.getRedeemScript().setBinary(redeemScript);
            txOut.getScriptCode().setBinary(scriptCode);
            txOut.getScriptPubKey().setBinary(scriptPubKey);
            txOut.getOutput().setHex(txOut.getValue().getHex() + txOut.getScriptPubKey().getHex(true));
        }
    };

    private AddressParser defaultAddressParser;

    private AddressParser customerAddressParser;

    private RawTransactionDTO rawTransactionDTO;

    private SignatureService signatureService;
    private Serializer preimageSerializer = new PreimageSerializer();

    private TransactionSerializer(RawTransactionDTO rawTransactionDTO) {
        this.rawTransactionDTO = rawTransactionDTO;
        this.signatureService = new BCSignatureService();
    }

    private TransactionSerializer(RawTransactionDTO rawTransactionDTO, String curveName) {
        this.rawTransactionDTO = rawTransactionDTO;
        this.signatureService = new BCSignatureService(curveName);
    }

    private TransactionSerializer(RawTransactionDTO rawTransactionDTO, AddressParser defaultAddressParser) {
        this(rawTransactionDTO);
        this.defaultAddressParser = defaultAddressParser;
    }

    private TransactionSerializer(RawTransactionDTO rawTransactionDTO, AddressParser defaultAddressParser,
            AddressParser customerAddressParser) {
        this(rawTransactionDTO, defaultAddressParser);
        this.customerAddressParser = customerAddressParser;
    }

    private TransactionSerializer(RawTransactionDTO rawTransactionDTO, AddressParser defaultAddressParser,
            AddressParser customerAddressParser, String curveName) {
        this(rawTransactionDTO, defaultAddressParser, customerAddressParser);
        this.signatureService = new BCSignatureService(curveName);
    }

    @Override
    public void serialize() {

        boolean isWitnessEnable = rawTransactionDTO.getTxIns().stream()
                .anyMatch(txIn -> txIn.getPreviousOutput().getTxOut().getAddress().getAddress().startsWith("2")
                        || txIn.getPreviousOutput().getTxOut().getAddress().getAddress().startsWith("3"));
        rawTransactionDTO.setWitnessEnable(isWitnessEnable);

        preimageSerializer.serialize();

        List<Item> joint = new ArrayList<>();

        serializeVersion(joint);
        serializeInputs(joint);
        serializeOutputs(joint);
        serializeWitnesses(joint);
        serializeLockTime(joint);

        String signedRawTx = joint.stream().filter(item -> item instanceof InternalItem).map(Item::getValue)
                .collect(Collectors.joining());

        log.debug("signedRawTx:\r\n{}\r\n", signedRawTx);

        rawTransactionDTO.getSignedRawTx().setHex(signedRawTx);

        if (log.isDebugEnabled()) {
            String debugSignedRawTx = joint.stream().map(item -> {
                if (item instanceof InternalItem) {
                    return String.join("\t\t", item.getName(), item.getValue(),
                            Utils.isEmpty(item.getDesc()) ? "" : item.getDesc());
                } else if (item instanceof DescItem) {
                    return item.getDesc();
                } else {
                    return "\r\n";
                }
            }).collect(Collectors.joining("\r\n"));

            log.debug("debug signedRawTx:\r\n{}\r\n", debugSignedRawTx);

            log.debug("debug signedRawTx length : {}", rawTransactionDTO.getSignedRawTx().getLength());

            log.debug("debug signedRawTx weight : {}", rawTransactionDTO.getWeight());

            log.debug("debug signedRawTx base size : {}", rawTransactionDTO.getBaseSize());

            log.debug("debug signedRawTx v size : {}", rawTransactionDTO.getVSize());
        }

        String hash = BinaryUtils.reverse(toHexString(BinaryUtils.sha256Twice(rawTransactionDTO.getSignedRawTx().getBinary())));

        rawTransactionDTO.setHash(hash);

        if (rawTransactionDTO.isWitnessEnable()) {
            String signedNoWitnessRawTx = joint.stream().filter(item -> item instanceof InternalItem)
                    .filter(item -> !item.isWitness()).map(Item::getValue).collect(Collectors.joining());

            log.debug("signedNoWitnessRawTx:\r\n{}\r\n", signedNoWitnessRawTx);

            rawTransactionDTO.getSignedNoWitnessRawTx().setHex(signedNoWitnessRawTx);

            String txid = BinaryUtils.reverse(toHexString(BinaryUtils.sha256Twice(rawTransactionDTO.getSignedNoWitnessRawTx().getBinary())));
            rawTransactionDTO.setTxId(txid);
        } else {
            rawTransactionDTO.setTxId(hash);
        }

        log.debug(rawTransactionDTO.getTxId());
    }

    private void serializeVersion(List<Item> joint) {
        joint.add(new InternalItem("version:", rawTransactionDTO.getVersion().getHex()));
        if (rawTransactionDTO.isWitnessEnable()) {
            joint.add(new InternalItem("marker:", rawTransactionDTO.getMarker().getHex(), true));
            joint.add(new InternalItem("flag:", rawTransactionDTO.getFlag().getHex(), true));
        }
    }

    private void serializeInputs(List<Item> joint) {

        int txInCount = rawTransactionDTO.getTxIns().size();
        if (txInCount > 0) {
            joint.add(new InternalItem("txInCount: ", BinaryUtils.compactSizeUintToHex(valueOf(txInCount)), "count :" + txInCount));
        }

        joint.add(new DescItem("================ tx in start ================"));

        for (RawTransactionDTO.TxInDTO txIn : rawTransactionDTO.getTxIns()) {

            joint.add(new InternalItem("preTxId + preIndex: ", txIn.getOutpoint().getHex(),
                    "reverse(" + txIn.getPreviousOutput().getHash() + ")"));
            ScriptType scriptType = txIn.getPreviousOutput().getTxOut().getAddress().getScriptType();
            if (scriptType.equals(ScriptType.P2PKH)) {
                String scriptSig = txIn.getWitnesses().stream().map(element -> element.getHex(true))
                        .collect(Collectors.joining(""));
                joint.add(new InternalItem("scriptSig: ", new RawTransactionDTO.Element(scriptSig).getHex(true)));
            } else if (scriptType.equals(ScriptType.P2SHP2WPKH)) {
                String redeemScriptHex = txIn.getPreviousOutput().getTxOut().getRedeemScript().getHex(true);
                joint.add(new InternalItem("scriptSig: ", new RawTransactionDTO.Element(redeemScriptHex).getHex(true)));
            }
            joint.add(new InternalItem("sequence: ", rawTransactionDTO.getSequence().getHex()));
        }
        joint.add(new DescItem("================ tx in end ================"));
    }

    private void serializeOutputs(List<Item> joint) {

        int txOutCount = rawTransactionDTO.getTxOuts().size();
        if (txOutCount > 0) {
            joint.add(new InternalItem("txOutCount: ", BinaryUtils.compactSizeUintToHex(valueOf(txOutCount)),
                    "count: " + txOutCount));
        }

        joint.add(new DescItem("================ tx out start ================"));
        for (RawTransactionDTO.TxOutDTO txOut : rawTransactionDTO.getTxOuts()) {

            if (txOut.isUsingCustomParser() && null != customerAddressParser) {
                customerAddressParser.parse(txOut);
            } else {
                defaultAddressParser.parse(txOut);
            }

            joint.add(new InternalItem("amount + script pub :", txOut.getOutput().getHex()));
        }
        joint.add(new DescItem("================ tx out end ================"));
    }

    private void serializeWitnesses(List<Item> joint) {

        if (rawTransactionDTO.isWitnessEnable()) {

            joint.add(new DescItem("================ witness start ================"));
            for (RawTransactionDTO.TxInDTO txIn : rawTransactionDTO.getTxIns()) {

                if (txIn.getPreviousOutput().getTxOut().getAddress().getScriptType().equals(ScriptType.P2PKH)) {
                    txIn.getWitnesses().clear();
                }

                int witnessCount = txIn.getWitnesses().size();

                joint.add(new InternalItem("witnessSize: ", BinaryUtils.compactSizeUintToHex(valueOf(witnessCount)),
                        "count: " + witnessCount, true));

                for (int i = 0; i < witnessCount; i++) {
                    joint.add(new InternalItem("witness[" + i + "]: ", txIn.getWitnesses().get(i).getHex(true), true));
                }
            }
            joint.add(new DescItem("================ witness end ================"));
        }
    }

    private void serializeLockTime(List<Item> joint) {
        joint.add(new InternalItem("lockTime:", rawTransactionDTO.getLockTime().getHex()));
    }

    public interface AddressParser {
        void parse(RawTransactionDTO.TxOutDTO txOut);
    }

    public static final class Builder {

        private Builder() {
        }

        public static TransactionSerializer of(RawTransactionDTO rawTransactionDTO,
                AddressParser defaultAddressParser) {
            return new TransactionSerializer(rawTransactionDTO, defaultAddressParser);
        }

        public static TransactionSerializer of(RawTransactionDTO rawTransactionDTO, AddressParser defaultAddressParser,
                AddressParser customAddressParser) {
            return new TransactionSerializer(rawTransactionDTO, defaultAddressParser, customAddressParser);
        }

        public static TransactionSerializer of(RawTransactionDTO rawTransactionDTO, AddressParser defaultAddressParser,
                AddressParser customAddressParser, String curveName) {
            return new TransactionSerializer(rawTransactionDTO, defaultAddressParser, customAddressParser, curveName);
        }
    }

    protected class PreimageSerializer implements Serializer {

        @Override
        public void serialize() {
            _serialize();
        }

        // see:
        // https://bitcoin.stackexchange.com/questions/71284/how-do-i-generate-the-bitcoin-cash-hash-preimage?rq=1
        // see: https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki
        // crazy for generating this fucking damn stupid code by william, so tired
        // yes I did it again for BitCoin, so stupid me
        private void _serialize() {

            List<Item> bip143Joint = new ArrayList<>(__bip143SerializeInputsJoint());
            bip143Joint.addAll(__bip143SerializeOutputsJoint());

            List<Item> legacyJoint = new ArrayList<>(__legacySerializeInputsJoint());
            legacyJoint.addAll(__legacySerializeOutputsJoint());

            rawTransactionDTO.getTxIns().forEach(txIn -> {

                String preimage;

                if (txIn.getPreviousOutput().getTxOut().getAddress().getScriptType().equals(ScriptType.P2SHP2WPKH)) {
                    preimage = bip143Joint.stream().map(item -> {
                        if (!Utils.isEmpty(item.getId()) && item.getId().equals("OUTPOINT")) {
                            return String.join("", txIn.getOutpoint().getHex(), txIn.getScriptCode().getHex(),
                                    txIn.getPreviousOutput().getTxOut().getValue().getHex());
                        } else {
                            return item.getValue();
                        }
                    }).collect(Collectors.joining());

                    log.debug("preimage :\r\n{}\r\n", preimage);

                    if (log.isDebugEnabled()) {
                        String debugPreImage = bip143Joint.stream()
                                .map(item -> String.join("\t\t", item.getName(), item.getValue(),
                                        Utils.isEmpty(item.getDesc()) ? "" : item.getDesc()))
                                .collect(Collectors.joining("\r\n"));
                        log.debug("debug bip143 preimage :\r\n{}\r\n", debugPreImage);
                    }
                } else {
                    preimage = legacyJoint.stream().map(item -> {
                        if (!Utils.isEmpty(item.getId()) && item.getId().equals(txIn.getOutpoint().getHex())) {
                            return txIn.getPreviousOutput().getTxOut().getScriptPubKey().getHex(true);
                        } else {
                            return item.getValue();
                        }
                    }).collect(Collectors.joining());

                    log.debug("preimage :\r\n{}\r\n", preimage);

                    if (log.isDebugEnabled()) {
                        String debugPreImage = legacyJoint.stream()
                                .map(item -> String.join("\t\t", item.getName(), item.getValue(),
                                        Utils.isEmpty(item.getDesc()) ? "" : item.getDesc()))
                                .collect(Collectors.joining("\r\n"));
                        log.debug("debug legacy preimage :\r\n{}\r\n", debugPreImage);
                    }
                }

                __signature(txIn, preimage);
            });
        }

        private List<Item> __bip143SerializeInputsJoint() {

            List<Item> inputsJoint = new ArrayList<>();

            Item nVersionItem = new InternalItem("nVersion:", rawTransactionDTO.getVersion().getHex());
            inputsJoint.add(nVersionItem);

            for (RawTransactionDTO.TxInDTO txIn : rawTransactionDTO.getTxIns()) {

                txIn.getOutpoint().setHex(BinaryUtils.reverse(txIn.getPreviousOutput().getHash().getHex())
                        + txIn.getPreviousOutput().getIndex().getHex());

                if (txIn.getPreviousOutput().getTxOut().isUsingCustomParser() && null != customerAddressParser) {
                    customerAddressParser.parse(txIn.getPreviousOutput().getTxOut());
                } else {
                    defaultAddressParser.parse(txIn.getPreviousOutput().getTxOut());
                }

                txIn.setScriptCode(txIn.getPreviousOutput().getTxOut().getScriptCode());
            }

            String prevouts = rawTransactionDTO.getTxIns().stream().map(txIn -> txIn.getOutpoint().getHex())
                    .collect(Collectors.joining());
            Item hashPrevoutsItem = new InternalItem("hashPrevouts:", toHexString(BinaryUtils.sha256Twice(decode(prevouts))),
                    "SHA256(SHA256(" + prevouts + "))");
            inputsJoint.add(hashPrevoutsItem);

            String sequence = rawTransactionDTO.getTxIns().stream()
                    .map(txIn -> rawTransactionDTO.getSequence().getHex()).collect(Collectors.joining());
            Item hashSequenceItem = new InternalItem("hashSequence:", toHexString(BinaryUtils.sha256Twice(decode(sequence))),
                    "SHA256(SHA256(" + sequence + "))");
            inputsJoint.add(hashSequenceItem);

            InternalItem item = new InternalItem("outpoint + scriptCode + amount :", "");
            item.setId("OUTPOINT");
            inputsJoint.add(item);

            Item nSequenceItem = new InternalItem("nSequence:", rawTransactionDTO.getSequence().getHex());
            inputsJoint.add(nSequenceItem);

            return inputsJoint;
        }

        private List<Item> __bip143SerializeOutputsJoint() {

            List<Item> outputsJoint = new ArrayList<>();

            List<String> rawOutputs = new ArrayList<>();

            for (RawTransactionDTO.TxOutDTO txOut : rawTransactionDTO.getTxOuts()) {

                if (txOut.isUsingCustomParser() && null != customerAddressParser) {
                    customerAddressParser.parse(txOut);
                } else {
                    defaultAddressParser.parse(txOut);
                }

                rawOutputs.add(txOut.getOutput().getHex());
            }

            String outputs = String.join("", rawOutputs);
            Item hashOuptputsItem = new InternalItem("hashOutputs:", toHexString(BinaryUtils.sha256Twice(decode(outputs))),
                    "SHA256(SHA256(" + outputs + "))");
            Item nLocktimeItem = new InternalItem("nLocktime:", rawTransactionDTO.getLockTime().getHex());
            Item sighashItem = new InternalItem("sighash:", BinaryUtils.uint32ToHex(rawTransactionDTO.getSigHashFlag().getValue()));

            outputsJoint.add(hashOuptputsItem);
            outputsJoint.add(nLocktimeItem);
            outputsJoint.add(sighashItem);

            return outputsJoint;
        }

        private List<Item> __legacySerializeInputsJoint() {

            List<Item> inputsJoint = new ArrayList<>();

            Item versionItem = new InternalItem("nVersion:", rawTransactionDTO.getVersion().getHex());
            inputsJoint.add(versionItem);

            int txInCount = rawTransactionDTO.getTxIns().size();
            if (txInCount > 0) {
                Item txInCountItem = new InternalItem("txInCount: ", BinaryUtils.compactSizeUintToHex(valueOf(txInCount)),
                        "count :" + txInCount);
                inputsJoint.add(txInCountItem);
            }

            for (RawTransactionDTO.TxInDTO txIn : rawTransactionDTO.getTxIns()) {

                txIn.getOutpoint().setHex(BinaryUtils.reverse(txIn.getPreviousOutput().getHash().getHex())
                        + txIn.getPreviousOutput().getIndex().getHex());

                if (txIn.getPreviousOutput().getTxOut().isUsingCustomParser() && null != customerAddressParser) {
                    customerAddressParser.parse(txIn.getPreviousOutput().getTxOut());
                } else {
                    defaultAddressParser.parse(txIn.getPreviousOutput().getTxOut());
                }

                Item prevoutsItem = new InternalItem("Prevout:", txIn.getOutpoint().getHex());
                inputsJoint.add(prevoutsItem);
                InternalItem scriptSigItem = new InternalItem("Script Sig:",
                        new RawTransactionDTO.NumberElement(BigInteger.ZERO, RawTransactionDTO.DataType.UINT_8)
                                .getHex());
                scriptSigItem.setId(txIn.getOutpoint().getHex());
                inputsJoint.add(scriptSigItem);
                inputsJoint.add(new InternalItem("Sequence:", rawTransactionDTO.getSequence().getHex()));
            }

            return inputsJoint;
        }

        private List<Item> __legacySerializeOutputsJoint() {

            List<Item> outputsJoint = new ArrayList<>();

            int txOutCount = rawTransactionDTO.getTxOuts().size();
            if (txOutCount > 0) {
                outputsJoint.add(new InternalItem("txOutCount: ", BinaryUtils.compactSizeUintToHex(valueOf(txOutCount)),
                        "count: " + txOutCount));
            }

            for (RawTransactionDTO.TxOutDTO txOut : rawTransactionDTO.getTxOuts()) {

                if (txOut.isUsingCustomParser() && null != customerAddressParser) {
                    customerAddressParser.parse(txOut);
                } else {
                    defaultAddressParser.parse(txOut);
                }

                outputsJoint.add(new InternalItem("amount + script pub :", txOut.getOutput().getHex()));
            }

            Item nLocktimeItem = new InternalItem("nLocktime:", rawTransactionDTO.getLockTime().getHex());
            Item sighashItem = new InternalItem("sighash:", BinaryUtils.uint32ToHex(rawTransactionDTO.getSigHashFlag().getValue()));

            outputsJoint.add(nLocktimeItem);
            outputsJoint.add(sighashItem);

            return outputsJoint;
        }

        private void __signature(RawTransactionDTO.TxInDTO txIn, String preimage) {
            byte[] preimageHash = BinaryUtils.sha256Twice(decode(preimage));

            log.debug("preimage hash : {}", toHexString(preimageHash));

            byte[] signature = signatureService.signature(txIn.getR().getBinary(), preimageHash);

            txIn.getSignature().setBinary(signature);

            // clean old witness before adding
            txIn.getWitnesses().clear();
            // witness 0: signature w/ sig hash
            txIn.getWitnesses().add(new RawTransactionDTO.Element(
                    txIn.getSignature().getHex() + rawTransactionDTO.getSigHashFlag().getHex()));
            // witness 1: public key
            txIn.getWitnesses().add(txIn.getPreviousOutput().getTxOut().getU());
        }
    }

    @Getter
    @Setter
    class InternalItem implements Item {

        private String id;
        private String name;
        private String value;
        private String desc;
        private boolean witness;

        InternalItem(String name, String value) {
            this.name = name;
            this.value = value;
        }

        InternalItem(String name, String value, String desc) {
            this(name, value);
            this.desc = desc;
        }

        InternalItem(String name, String value, boolean witness) {
            this.name = name;
            this.value = value;
            this.witness = witness;
        }

        InternalItem(String name, String value, String desc, boolean witness) {
            this(name, value, witness);
            this.desc = desc;
        }
    }

    @Getter
    @Setter
    class DescItem implements Item {

        private String id;
        private String name;
        private String value;
        private String desc;

        DescItem(String desc) {
            this.desc = desc;
        }

        public boolean isWitness() {
            return false;
        }
    }
}