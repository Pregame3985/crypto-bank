package com.tokimi.chain.service.tx;

import com.google.common.collect.Lists;
import com.tokimi.chain.model.ChainTxWrapper;
import com.tokimi.chain.rpc.model.eth.request.GasPriceRequest;
import com.tokimi.chain.rpc.model.eth.request.GetTransactionByHashRequest;
import com.tokimi.chain.rpc.model.eth.request.GetTransactionCountRequest;
import com.tokimi.chain.rpc.model.eth.request.GetTransactionReceiptRequest;
import com.tokimi.chain.rpc.model.eth.request.SendRawTransactionRpcRequest;
import com.tokimi.chain.rpc.model.eth.response.GasPriceResponse;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionCountResponse;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionReceiptResponse;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionResponse;
import com.tokimi.chain.rpc.model.eth.response.SendRawTransactionRpcResponse;
import com.tokimi.chain.rpc.model.eth.response.Transaction;
import com.tokimi.common.ErrorConstants;
import com.tokimi.common.ErrorDTO;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.ChainTransaction;
import com.tokimi.common.chain.model.RawTx;
import com.tokimi.common.chain.model.ReceiverDTO;
import com.tokimi.common.chain.model.SenderDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.model.WithdrawRequestDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sequence.SequenceService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.common.network.ChainError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static com.tokimi.common.ErrorConstants.TXID_NOT_SAME;
import static com.tokimi.common.ErrorConstants.TX_FEE_TOO_LOW;
import static com.tokimi.common.ErrorConstants.TX_INCLUDE_DUST;
import static com.tokimi.common.ErrorConstants.TX_OTHER_ERROR;
import static com.tokimi.common.ErrorConstants.TX_REJECTED;

/**
 * @author william
 */
@Slf4j
@Service
public class NativeTransactionService extends TransactionServiceAdapter {

    private static final BigInteger GAS_PRICE = BigInteger.valueOf(20_000_000_000L);

    @Resource
    protected ChainService chainService;

    @Resource
    private List<WalletService> walletServiceList;

    @Resource
    private WalletService walletService;

    @Resource
    protected BlockService blockService;

    @Resource
    private SequenceService sequenceService;

    @Resource
    private AssetService assetService;

    @Override
    public TransactionDTO assemble(Long tokenId, Integer tokenType, Integer txType, List<WithdrawRequestDTO> requests) {

//        List<String> senderAddresses = ManagerHub.getInstance().getAssetDTO().getAddresses();

        TransactionDTO transactionDTO = new TransactionDTO();

//        String senderAddress;
//
//        if (TxType.SWEEP.getValue().equals(txType)) {
//            senderAddress = requests.get(0).getFromAddress();
//        } else {
//            senderAddress = senderAddresses.get(randomizer.nextInt(senderAddresses.size()));
//        }
//
//        log.info("withdraw type is : {},  sender address is : {}", txType, senderAddress);
//
//        WithdrawRequestDTO request = requests.get(0);
//
//        BigDecimal withdrawAmount = request.getAmount();
//
//        if (withdrawAmount.compareTo(BigDecimal.ZERO) <= 0) {
//            transactionDTO.setError(new ErrorDTO(ErrorConstants.WITHDRAW_AMOUNT_NOT_VALID,
//                    String.format("[withdraw amount not valid, amount %s]", withdrawAmount.toPlainString())));
//            return transactionDTO;
//        }
//
//        BigDecimal balance = walletService.balance(senderAddress, tokenId);
//
//        if (balance.compareTo(withdrawAmount) < 0) {
//            transactionDTO.setError(new ErrorDTO(ErrorConstants.BALANCE_NOT_ENOUGH,
//                    String.format("[%s balance {%s} not enough]", senderAddress, balance)));
//            return transactionDTO;
//        }
//
//        try {
//            Long nonce = sequenceService.get(senderAddress);
//            BigInteger gasPrice = GAS_PRICE;
//
//            GasPriceResponse res = gasPrice();
//            if (res.isSuccess()) {
//                gasPrice = BigInteger.valueOf(BinaryUtils.hexToLong(res.getResult()));
//            }
//
//            BigDecimal precision = walletService.precision(chainService.getAssetId());
//            int scale = walletService.scale(chainService.getAssetId());
//
//            Credentials credentials = Credentials.create(ECKeyPair.create(walletService.generateR(senderAddress)));
//            org.web3j.crypto.RawTransaction rawTransaction = org.web3j.crypto.RawTransaction.createEtherTransaction(BigInteger.valueOf(nonce++), gasPrice,
//                    GAS_LIMIT, request.getToAddress(), withdrawAmount.multiply(precision).toBigInteger());
//            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
//            byte[] txHash = Hash.sha3(signedMessage);
//
//            SenderDTO sender = new SenderDTO();
//            sender.setAddress(senderAddress);
//            sender.setAmount(withdrawAmount.negate());
//            sender.setType(request.getType());
//
//            ReceiverDTO receiver = new ReceiverDTO();
//            receiver.setAddress(request.getToAddress());
//            receiver.setAmount(withdrawAmount);
//            receiver.setRequestId(request.getId());
//            receiver.setMemo(request.getMemo());
//            receiver.setType(request.getType());
//            receiver.setUserId(request.getUserId());
//
//            transactionDTO.setSenders(Lists.newArrayList(sender));
//            transactionDTO.setReceivers(Lists.newArrayList(receiver));
//
//            transactionDTO.setSenderAddress(senderAddress);
//            transactionDTO.setTxid(Numeric.toHexString(txHash));
//            transactionDTO.setSignedRawTx(Numeric.toHexString(signedMessage));
//            transactionDTO
//                    .setFee(new BigDecimal(gasPrice.multiply(GAS_LIMIT)).divide(precision, scale, RoundingMode.DOWN));
//
//            sequenceService.update(senderAddress, nonce);
//
//        } catch (Exception e) {
//            transactionDTO.setError(new ErrorDTO(ErrorConstants.TX_OTHER_ERROR, e.getMessage()));
//            return transactionDTO;
//        }

        return transactionDTO;
    }

    @Override
    public TransactionDTO assemble(WithdrawRequestDTO dto) {

        TransactionDTO transactionDTO = new TransactionDTO();

        Long assetId = dto.getTokenId();
        BigDecimal amount = dto.getAmount();
        String senderAddress = dto.getFromAddress();
        String receiverAddress = dto.getToAddress();

        log.debug("assert id : {},  withdraw amount : {},  from address : {}, to address : {}",
                assetId, amount, senderAddress, receiverAddress);

        // TODO: Move to up level
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            transactionDTO.setError(new ErrorDTO(ErrorConstants.WITHDRAW_AMOUNT_NOT_VALID,
                    String.format("[withdraw amount not valid, amount %s]", amount.toPlainString())));
            return transactionDTO;
        }

        // TODO: Move to up level
        BigDecimal balance = walletService.balance(senderAddress, assetId);

        if (balance.compareTo(amount) < 0) {
            transactionDTO.setError(new ErrorDTO(ErrorConstants.BALANCE_NOT_ENOUGH,
                    String.format("[%s balance {%s} not enough]", senderAddress, balance)));
            return transactionDTO;
        }

        try {
            Long txCount = sequenceService.get(senderAddress);
            if (txCount < 0) {
                String msg = String.format("[%s tx count {%d} not valid]", senderAddress, txCount);
                transactionDTO.setError(new ErrorDTO(ErrorConstants.FEE_RATE_NOT_READY, msg));
                log.debug("{}", msg);
                return transactionDTO;
            }

            BigInteger gasPrice = gasPrice();
            if (gasPrice.compareTo(BigInteger.ZERO) <= 0) {
                String msg = String.format("[%s gas price {%s} not ready]", senderAddress, gasPrice.toString());
                transactionDTO.setError(new ErrorDTO(ErrorConstants.FEE_RATE_NOT_READY, msg));
                log.debug("{}", msg);
                return transactionDTO;
            }

            BigDecimal precision = walletService.precision(assetId);
            int scale = walletService.scale(assetId);

            Credentials credentials = Credentials.create(ECKeyPair.create(walletService.generateR(senderAddress)));
            RawTransaction rawTransaction = buildRawTransaction(assetId, BigInteger.valueOf(txCount), gasPrice, getGasLimit(), receiverAddress, amount.multiply(precision).toBigInteger());

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            byte[] txHash = Hash.sha3(signedMessage);

            SenderDTO sender = new SenderDTO();
            sender.setAddress(senderAddress);
            sender.setAmount(amount.negate());
            sender.setType(dto.getTxType().getValue());

            ReceiverDTO receiver = new ReceiverDTO();
            receiver.setAddress(receiverAddress);
            receiver.setAmount(amount);
            receiver.setRequestId(dto.getId());
            receiver.setType(dto.getTxType().getValue());
            receiver.setUserId(dto.getUserId());

            transactionDTO.setSenders(Lists.newArrayList(sender));
            transactionDTO.setReceivers(Lists.newArrayList(receiver));

            transactionDTO.setSenderAddress(senderAddress);
            transactionDTO.setTxid(Numeric.toHexString(txHash));
            transactionDTO.setSignedRawTx(Numeric.toHexString(signedMessage));
            transactionDTO
                    .setFee(new BigDecimal(gasPrice.multiply(getGasLimit())).divide(precision, scale, RoundingMode.DOWN));

            sequenceService.update(senderAddress, txCount + 1);

        } catch (Exception e) {
            transactionDTO.setError(new ErrorDTO(ErrorConstants.TX_OTHER_ERROR, e.getMessage()));
            return transactionDTO;
        }

        return transactionDTO;
    }

    protected RawTransaction buildRawTransaction(Long assetId, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value) {
        return RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to, value);
    }

    BigInteger getGasLimit() {
        return BigInteger.valueOf(21000L);
    }

    @Override
    public TransactionDTO get(String txid, Function<String, Boolean> addressFilter,
                              Function<String, Boolean> assetFilter) {

        GetTransactionResponse txRes = getTx(txid);

        if (!txRes.isSuccess()) {
            TransactionDTO transactionDTO = new TransactionDTO();

            ErrorDTO fullError = txRes.getFullError();
            transactionDTO.setError(fullError);

            return transactionDTO;
        }

        ChainTransaction<? extends RawTx> chainTx = new ChainTxWrapper(chainService, blockService, txRes.getResult());

        return parseTx(() -> chainTx, assetFilter, addressFilter);
    }

    @Override
    protected void parseSendersAndReceivers(TransactionDTO transactionDTO, RawTx rawTransaction,
                                            Function<String, Boolean> assetFilter, Function<String, Boolean> addressFilter) {

        Transaction chainTx = (Transaction) rawTransaction;

        transactionDTO.setTokenId(chainService.getAssetId());
//        transactionDTO.setConfirmations(ManagerHub.getInstance().getRemoteBestBlock().getHeight() - chainTx.getBlockHeight() + 1);

        SenderDTO sender = new SenderDTO();
        sender.setAddress(chainTx.getFrom());
        transactionDTO.setSenders(Lists.newArrayList(sender));

        BigDecimal precision = walletService.precision(chainService.getAssetId());
        int scale = walletService.scale(chainService.getAssetId());

        if (null != addressFilter) {

            if (!StringUtils.isEmpty(chainTx.getTo()) && addressFilter.apply(chainTx.getTo().toLowerCase())) {
                ReceiverDTO receiver = new ReceiverDTO();
                receiver.setAddress(chainTx.getTo());
                receiver.setAmount(new BigDecimal(BinaryUtils.hexToLong(chainTx.getValue())).divide(precision, scale,
                        RoundingMode.DOWN));
                receiver.setIndex(0);
                receiver.setTokimiAddress(addressFilter.apply(receiver.getAddress().toLowerCase()));
                transactionDTO.setReceivers(Lists.newArrayList(receiver));
            }
        } else {
            ReceiverDTO receiver = new ReceiverDTO();
            receiver.setAddress(chainTx.getTo());
            receiver.setAmount(new BigDecimal(BinaryUtils.hexToLong(chainTx.getValue())).divide(precision, scale,
                    RoundingMode.DOWN));
            receiver.setIndex(0);
            transactionDTO.setReceivers(Lists.newArrayList(receiver));
        }

        checkStatus(transactionDTO, chainTx);

        transactionDTO.setTokenId(assetService.getManagerAsset().getId());
    }

    protected void checkStatus(TransactionDTO transactionDTO, Transaction chainTx) {
        if (!Utils.isEmpty(transactionDTO.getReceivers())) {
            GetTransactionReceiptResponse txReceiptRes = getTxReceipt(chainTx.getTxid());

            if (!txReceiptRes.isSuccess()) {
                ErrorDTO fullError = txReceiptRes.getFullError();
                transactionDTO.setError(fullError);

                return;
            }

            Transaction txReceipt = txReceiptRes.getResult();

            if (null == txReceipt) {
                return;
            }

            chainTx.setContractAddress(txReceipt.getContractAddress());
            chainTx.setLogs(txReceipt.getLogs());
            chainTx.setLogsBloom(txReceipt.getLogsBloom());
            chainTx.setGasUsed(txReceipt.getGasUsed());
            chainTx.setCumulativeGasUsed(txReceipt.getCumulativeGasUsed());
            chainTx.setStatus(txReceipt.getStatus());

            transactionDTO.setValid("0x1".equals(txReceipt.getStatus()));
        }
    }

    protected GetTransactionCountResponse getTxCount(String address) {

        GetTransactionCountRequest request = new GetTransactionCountRequest(address);
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }

    protected GetTransactionReceiptResponse getTxReceipt(String txid) {

        GetTransactionReceiptRequest request = new GetTransactionReceiptRequest(txid);
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }

    protected GetTransactionResponse getTx(String txid) {

        GetTransactionByHashRequest request = new GetTransactionByHashRequest(txid);
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }

    protected BigInteger gasPrice() {
        GasPriceResponse res = _gasPrice();

        BigInteger gasPrice = BigInteger.ZERO;

        if (res.isSuccess()) {
            gasPrice = Numeric.toBigInt(res.getResult());
        }

        return gasPrice;
    }

    private GasPriceResponse _gasPrice() {

        GasPriceRequest request = new GasPriceRequest();
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }

    protected GetTransactionCountResponse getNonce(String address) {

        GetTransactionCountRequest request = new GetTransactionCountRequest(address);
        return chainService.getJsonRpcAgent().sendToNetwork(request);
    }

    @Override
    protected boolean sendRawTransaction(String rawTx, TransactionDTO transactionDTO) {

        boolean result = true;

        if (Utils.isEmpty(rawTx)) {

            transactionDTO.setError(new ErrorDTO(TX_OTHER_ERROR, "raw tx should not be empty"));
            return false;
        }

        if (Utils.isEmpty(transactionDTO.getTxid())) {

            transactionDTO.setError(new ErrorDTO(TX_OTHER_ERROR, "txid should not be empty"));
            return false;
        }

        SendRawTransactionRpcRequest request = new SendRawTransactionRpcRequest(rawTx);

        // https://bitcoin.org/en/developer-reference#sendrawtransaction
        SendRawTransactionRpcResponse sendRawTxResponse = chainService.getJsonRpcAgent().sendToNetwork(request);

        String txId = null;

        if (!sendRawTxResponse.isSuccess()) {

            ChainError chainError = sendRawTxResponse.getChainError();

            if (chainError.getCode().equals(-27)
                    && chainError.getMessage().equalsIgnoreCase("transaction already in block chain")) {
                // tx already in block chain as successful
                log.warn(chainError.getMessage());
                txId = transactionDTO.getTxid();
            } else if (chainError.getCode().equals(-26)) { // -26 : fee too low

                ErrorDTO error;

                if (chainError.getMessage().contains("64")) { // insufficient priority
                    error = new ErrorDTO(TX_FEE_TOO_LOW, "fee to low");
                } else if (chainError.getMessage().contains("66")) { // dust
                    error = new ErrorDTO(TX_INCLUDE_DUST, "include dust");
                } else {
                    error = new ErrorDTO(TX_OTHER_ERROR, "other error see desc");
                }

                // TODO: error code
//                error.setCode(ErrorConstants.valueOf()).setChainCode(chainError.getCode());
                error.setMessage(chainError.getMessage());
                transactionDTO.setError(error);
                result = false;
            } else {
                transactionDTO.setError(sendRawTxResponse.getFullError());
                result = false;
            }
        } else {
            txId = sendRawTxResponse.getResult();
        }

        if (Utils.isEmpty(txId)) {
            transactionDTO.setError(new ErrorDTO(TX_REJECTED, "transaction was rejected by the node"));
            result = false;
        } else {
            if (!Objects.equals(txId, transactionDTO.getTxid())) {
                transactionDTO.setError(new ErrorDTO(TXID_NOT_SAME,
                        "tx id expected: " + transactionDTO.getTxid() + ", actual " + txId));
                result = false;
            }
        }

        return result;
    }
}
