package com.tokimi.chain.service.tx;

import com.tokimi.chain.model.RawTransactionDTO;
import com.tokimi.common.ChainManagerException;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.ReceiverDTO;
import com.tokimi.common.chain.model.SenderDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.model.WithdrawRequestDTO;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.wallet.UtxoWalletService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.common.chain.utils.ScriptType;
import com.tokimi.common.chain.utils.TokenType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.tokimi.common.ErrorConstants.BALANCE_NOT_ENOUGH;
import static com.tokimi.common.ErrorConstants.FEE_BALANCE_NOT_ENOUGH;
import static org.bouncycastle.util.encoders.Hex.decode;

/**
 * @author william
 */
@Slf4j
public class TransactionBuilder {

    public static final int FIXED_MIN_BYTES = 546;
    public static final TransactionSerializer.AddressParser OMNI_ADDRESS_PARSER = (txOut) -> {

        RawTransactionDTO.OmniTxOutDTO omniTxOutDTO = (RawTransactionDTO.OmniTxOutDTO) txOut;
        omniTxOutDTO.getAddress().setScriptType(ScriptType.OMNI);

        // see:
        // https://medium.com/omnilayer/optimizing-the-payloads-of-omni-layer-transactions-ccb0a867da5d
        String hex = String.join("",
                Arrays.asList(omniTxOutDTO.getMark().getHex(), BinaryUtils.reverse(omniTxOutDTO.getVersion().getHex()),
                        BinaryUtils.reverse(omniTxOutDTO.getType().getHex()), BinaryUtils.reverse(omniTxOutDTO.getIdentifier().getHex()),
                        BinaryUtils.reverse(omniTxOutDTO.getOmniValue().getHex())));

        byte[] bytes = ScriptBuilder.createOmniPayload(decode(hex)).getProgram();

        txOut.getScriptPubKey().setBinary(bytes);
        txOut.getOutput().setHex(txOut.getValue().getHex() + txOut.getScriptPubKey().getHex(true));
    };
    private final List<To> tos = new ArrayList<>();
    private final List<WalletService> walletServices;
    private final ChainService chainService;
    private final TokenType tokenType;
    private String fromAddress;
    private String changeAddress;
    private List<WithdrawRequestDTO> requests;

    private TransactionBuilder(List<WalletService> walletServices, ChainService chainService, TokenType tokenType) {
        this.walletServices = walletServices;
        this.chainService = chainService;
        this.tokenType = tokenType;
    }

    public static TransactionBuilder create(List<WalletService> walletServices, ChainService chainService,
                                            TokenType tokenType) {
        return new TransactionBuilder(walletServices, chainService, tokenType);
    }

    public TransactionBuilder from(String address) {
        this.fromAddress = address;
        return this;
    }

    public TransactionBuilder change(String address) {
        this.changeAddress = address;
        return this;
    }

    public TransactionBuilder add(List<WithdrawRequestDTO> requests) {

        this.requests = requests;

        if (tokenType.equals(TokenType.TOKEN)) {

            this.tos.add(new TransactionBuilder.To("", requests.get(0).getAmount()));
            this.tos.add(new TransactionBuilder.To(requests.get(0).getToAddress(),
                    BigDecimal.valueOf(FIXED_MIN_BYTES).divide(BigDecimal.TEN.pow(8))));

        } else if (tokenType.equals(TokenType.NATIVE)) {

            Map<String, BigDecimal> inputs = new HashMap<>();

            for (WithdrawRequestDTO request : requests) {
                String address = request.getToAddress();
                if (inputs.containsKey(address)) {
                    BigDecimal value = inputs.get(address);
                    value = value.add(request.getAmount());
                    inputs.put(address, value);
                } else {
                    inputs.put(address, request.getAmount());
                }
            }

            for (String address : inputs.keySet()) {
                this.tos.add(new To(address, inputs.get(address)));
            }
        }

        return this;
    }

    private UtxoWalletService getNativeWalletService() {
        return (UtxoWalletService) walletServices.stream().filter(ws -> ws instanceof UtxoWalletService).findAny()
                .get();
    }

    // private BalanceWalletService getTokenWalletService() {
    // return (BalanceWalletService) walletServices.stream().filter(ws -> ws
    // instanceof BalanceWalletService).findAny()
    // .get();
    // }

    public TransactionDTO build() {

        // 1. build raw transaction
        RawTransactionDTO rawTransaction = new RawTransactionDTO();

        // ensure last tx out is the receiver
        List<RawTransactionDTO.TxOutDTO> txOuts = new ArrayList<>();

        BigDecimal sum = BigDecimal.ZERO;

        for (To to : tos) {
            if (!Utils.isEmpty(to.address)) {

                RawTransactionDTO.TxOutDTO txOut = new RawTransactionDTO.TxOutDTO();

                txOut.getAddress().setAddress(to.address);
                txOut.getValue().setValue(to.amount.multiply(BigDecimal.TEN.pow(8)).toBigInteger());

                txOuts.add(txOut);
                sum = sum.add(to.amount);
            } else {
                RawTransactionDTO.OmniTxOutDTO omniTxOut = new RawTransactionDTO.OmniTxOutDTO();

                // omniTxOut.getIdentifier()
                // .setValue(new
                // BigInteger(getTokenWalletService().getContract(chainService.getTokenId())));
                // omniTxOut.getOmniValue().setValue(to.amount.multiply(BigDecimal.TEN.pow(8)).toBigInteger());
                // omniTxOut.getAddress().setScriptType(OMNI);

                txOuts.add(omniTxOut);
            }
        }

        // 2. estimate fee
        BigDecimal fee;
        List<SenderDTO> senders = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int estimateTxSize = 100 + (i + 1) * 150;
            fee = getNativeWalletService().estimateFee(estimateTxSize);

            // 3. fund transaction in loop
            if (Utils.isEmpty(this.fromAddress)) {
                senders = getNativeWalletService().fundWithAnyone(chainService.getAssetId(), sum.add(fee));
            } else {
                senders = getNativeWalletService().fundWithAddress(chainService.getAssetId(), sum.add(fee),
                        this.fromAddress);
            }

            BigDecimal senderAmounts = senders.stream().map(SenderDTO::getAmount).reduce(BigDecimal.ZERO,
                    BigDecimal::add);

            log.debug("sender amount : {}, need amount : {}", senderAmounts, sum.add(fee));

            if (senders.size() > 0 && senders.size() <= i + 1) {
                break;
            }

            getNativeWalletService().releaseFund(chainService.getAssetId(),
                    senders.stream().map(SenderDTO::getUtxoId).collect(Collectors.toList()));
        }

        if (Utils.isEmpty(senders)) {
            if (tokenType.equals(TokenType.NATIVE)) {
                throw new ChainManagerException(BALANCE_NOT_ENOUGH, "balance not enough!!!!");
            } else {
                throw new ChainManagerException(FEE_BALANCE_NOT_ENOUGH, "fee balance not enough!!!!");
            }
        }

        // if (tokenType.equals(TOKEN)) {
        // BigDecimal balance =
        // getTokenWalletService().balance(senders.get(0).getAddress(),
        // chainService.getTokenId());
        // if (balance.compareTo(new BigDecimal(requests.get(0).getAmount())) < 0) {
        // getNativeWalletService().releaseFund(chainService.getTokenId(),
        // senders.stream().map(SenderDTO::getUtxoId).collect(Collectors.toList()));
        // throw new ChainManagerException(BALANCE_NOT_ENOUGH, "balance not
        // enough!!!!");
        // }
        // }

        BigDecimal senderSum = BigDecimal.ZERO;

        for (SenderDTO sender : senders) {

            RawTransactionDTO.TxInDTO txIn = new RawTransactionDTO.TxInDTO();
            txIn.getPreviousOutput().getHash().setHex(sender.getTxid());
            txIn.getPreviousOutput().getIndex().setValue(BigInteger.valueOf(sender.getIndex()));
            txIn.getPreviousOutput().getTxOut().getAddress().setAddress(sender.getAddress());
            txIn.getPreviousOutput().getTxOut().getValue()
                    .setValue(sender.getAmount().multiply(BigDecimal.TEN.pow(8)).toBigInteger());

            // 4. get r u
            txIn.getR().setBinary(getNativeWalletService().generateR(sender.getAddress()));
            txIn.getPreviousOutput().getTxOut().getU()
                    .setBinary(getNativeWalletService().generateU(sender.getAddress()));

            rawTransaction.getTxIns().add(txIn);

            senderSum = senderSum.add(sender.getAmount());
        }

        // 5. add change address
        int estimateTxSize = 100 + senders.size() * 150;
        fee = getNativeWalletService().estimateFee(estimateTxSize);

        RawTransactionDTO.TxOutDTO changeTxOut = new RawTransactionDTO.TxOutDTO();
        changeTxOut.getAddress().setAddress(this.changeAddress);
        changeTxOut.getValue()
                .setValue(senderSum.subtract(sum).subtract(fee).multiply(BigDecimal.TEN.pow(8)).toBigInteger());
        rawTransaction.getTxOuts().add(changeTxOut);
        rawTransaction.getTxOuts().addAll(txOuts);

        // 6. sign transaction for estimate vsize
        TransactionSerializer.Builder.of(rawTransaction, OMNI_ADDRESS_PARSER).serialize();

        // 7. re-calculate the real transaction fee
        fee = getNativeWalletService().estimateFee(rawTransaction.getVSize().intValue());
        changeTxOut.getValue()
                .setValue(senderSum.subtract(sum).subtract(fee).multiply(BigDecimal.TEN.pow(8)).toBigInteger());

        // 8. re-sign transaction for final
        TransactionSerializer.Builder.of(rawTransaction, OMNI_ADDRESS_PARSER).serialize();

        TransactionDTO tx = new TransactionDTO();

        tx.setSenders(senders);

        List<ReceiverDTO> receivers = new ArrayList<>();

        WithdrawRequestDTO request = null;
        if (requests.size() == 1) {
            request = requests.get(0);
        }

        for (int i = 0; i < rawTransaction.getTxOuts().size(); i++) {

            RawTransactionDTO.TxOutDTO txOut = rawTransaction.getTxOuts().get(i);

            String address = txOut.getAddress().getAddress();

            if (!Utils.isEmpty(address) && null != request) {
                ReceiverDTO receiver = new ReceiverDTO();
                receiver.setAddress(address);
                receiver.setAmount(new BigDecimal(txOut.getValue().getValue().divide(BigInteger.TEN.pow(8))));
                receiver.setRequestId(request.getId());
                receiver.setMemo(request.getMemo());
                receiver.setType(request.getType());
                receiver.setUserId(request.getUserId());
                receiver.setIndex(i);
                receivers.add(receiver);
            }
        }

        tx.setReceivers(receivers);

        tx.setTxid(rawTransaction.getTxId());
        tx.setSignedRawTx(rawTransaction.getSignedRawTx().getHex());
        tx.setFee(fee);
        tx.setValid(true);

        return tx;
    }

    @RequiredArgsConstructor
    private class To {

        @NonNull
        public String address;

        @NonNull
        public BigDecimal amount;
    }
}
