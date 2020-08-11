package com.tokimi.chain.service.tx;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.Transaction;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.AssetDTO;
import com.tokimi.common.chain.model.PropertyDTO;
import com.tokimi.common.chain.model.RawTx;
import com.tokimi.common.chain.model.ReceiverDTO;
import com.tokimi.common.chain.model.SenderDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.wallet.WalletService;
import com.tokimi.config.ManagerHub;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.RawTransaction;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

/**
 * @author william
 */
@Slf4j
@Service
public class TokenTransactionService extends NativeTransactionService {

    @Resource
    private WalletService walletService;

    @Resource
    private AssetService assetService;

    @Override
    protected RawTransaction buildRawTransaction(Long assetId, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value) {
        AssetDTO asset = assetService.getAsset(assetId);
        String data = _encodeTransferData(to, value);
        return RawTransaction.createTransaction(nonce, gasPrice, gasLimit, asset.getProperties().get(0).getKey(), data);
    }

    private static String _encodeTransferData(String toAddress, BigInteger sum) {
        org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                "transfer",  // function we're calling
                Arrays.asList(new Address(toAddress), new Uint256(sum)),  // Parameters to pass as Solidity Types
                Collections.singletonList(new TypeReference<Bool>() {
                }));
        return FunctionEncoder.encode(function);
    }

    BigInteger getGasLimit() {
        return BigInteger.valueOf(60000L);
    }

    @Override
    protected void parseSendersAndReceivers(TransactionDTO transactionDTO, RawTx rawTransaction,
                                            Function<String, Boolean> assetFilter, Function<String, Boolean> addressFilter) {

        Transaction chainTx = (Transaction) rawTransaction;

        for (AssetDTO assetDTO : assetService.getMyAssets()) {
            for (PropertyDTO propertyDTO : assetDTO.getProperties()) {
                if (propertyDTO.getKey().equalsIgnoreCase(chainTx.getTo())) {
                    transactionDTO.setTokenId(propertyDTO.getTokenId());
                    break;
                }
            }
        }

        if (null == transactionDTO.getTokenId()) {
            return;
        }

        transactionDTO.setConfirmations(ManagerHub.getInstance().getRemoteBestBlock().getHeight() - transactionDTO.getBlockheight() + 1);

        SenderDTO sender = new SenderDTO();
        sender.setAddress(chainTx.getFrom());
        transactionDTO.setSenders(Lists.newArrayList(sender));

        // TODO:
        BigDecimal precision = walletService.precision(transactionDTO.getTokenId());
        int scale = walletService.scale(transactionDTO.getTokenId());

        ContractInfo contractInfo = parseInput(chainTx.getInput());

        if (null != contractInfo && !Utils.isEmpty(contractInfo.getTo())) {
            if (null != addressFilter) {
                if (!StringUtils.isEmpty(chainTx.getTo()) && addressFilter.apply(contractInfo.getTo().toLowerCase())) {
                    ReceiverDTO receiver = new ReceiverDTO();
                    receiver.setAddress(contractInfo.getTo());
                    receiver.setAmount(new BigDecimal(contractInfo.amount).divide(precision, scale,
                            RoundingMode.DOWN));
                    receiver.setIndex(0);
                    receiver.setTokimiAddress(addressFilter.apply(receiver.getAddress().toLowerCase()));
                    transactionDTO.setReceivers(Lists.newArrayList(receiver));
                }
            } else {
                ReceiverDTO receiver = new ReceiverDTO();
                receiver.setAddress(contractInfo.getTo());
                receiver.setAmount(new BigDecimal(contractInfo.amount).divide(precision, scale,
                        RoundingMode.DOWN));
                receiver.setIndex(0);
                transactionDTO.setReceivers(Lists.newArrayList(receiver));
            }
        }

        checkStatus(transactionDTO, chainTx);
    }

    @Getter
    @Setter
    @ToString
    @RequiredArgsConstructor
    private static class ContractInfo {

        @NonNull
        private String to;

        @NonNull
        private BigInteger amount;
    }

    private ContractInfo parseInput(String input) {

        if (Utils.isEmpty(input) || input.length() != 138) {
            return null;
        }

        String to = input.substring(10, 74);
        String value = input.substring(74, 138);

        Method refMethod;
        try {
            refMethod = TypeDecoder.class.getDeclaredMethod("decode", String.class, int.class, Class.class);
            refMethod.setAccessible(true);

            Address address = (Address) refMethod.invoke(null, to, 0, Address.class);
            Uint256 amount = (Uint256) refMethod.invoke(null, value, 0, Uint256.class);

            return new ContractInfo(address.getValue(), amount.getValue());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
