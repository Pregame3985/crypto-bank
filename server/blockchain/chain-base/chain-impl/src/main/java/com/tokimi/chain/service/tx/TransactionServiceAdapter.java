package com.tokimi.chain.service.tx;

import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.BlockDTO;
import com.tokimi.common.chain.model.ChainTransaction;
import com.tokimi.common.chain.model.RawTx;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.service.tx.TransactionService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author william
 */
@Slf4j
public abstract class TransactionServiceAdapter implements TransactionService {

    @Setter
    @Resource
    protected ChainService chainService;

    @Setter
    @Resource
    protected BlockService blockService;

    @Override
    public TransactionDTO get(String txid) {
        return get(txid, null, null);
    }

    @Override
    public TransactionDTO get(String txid, Function<String, Boolean> addressFilter) {
        return get(txid, addressFilter, null);
    }

    @Override
    public TransactionDTO parseTx(Supplier<ChainTransaction<? extends RawTx>> txFunc,
                                  Function<String, Boolean> assetFilter, Function<String, Boolean> addressFilter) {

        ChainTransaction<? extends RawTx> chainTxWrapper = txFunc.get();

        RawTx rawTransaction = chainTxWrapper.getTx();

        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setTxid(chainTxWrapper.getTxid());
        transactionDTO.setBlockheight(chainTxWrapper.getBlockHeight());
        transactionDTO.setBlockhash(chainTxWrapper.getBlockHash());
        transactionDTO.setTokenType(chainTxWrapper.getTokenType());
        transactionDTO.setConfirmations(chainTxWrapper.getConfirmation());
        transactionDTO.setValid(chainTxWrapper.isValid());

        if (null == transactionDTO.getBlockheight() || transactionDTO.getBlockheight() == 0
                || Utils.isEmpty(transactionDTO.getBlockhash())) {
            BlockDTO block = blockService.getBlock(transactionDTO.getBlockhash());
            transactionDTO.setBlockheight(block.getHeight());
            transactionDTO.setBlockhash(block.getHash());
        }

        parseSendersAndReceivers(transactionDTO, rawTransaction, assetFilter, addressFilter);

        return transactionDTO;
    }

    @Override
    public void send(TransactionDTO transactionDTO) {

        // https://bitcoin.org/en/developer-reference#sendrawtransaction
        boolean result = sendRawTransaction(transactionDTO.getSignedRawTx(), transactionDTO);

        if (result) {
            log.info("sent tx {} success", transactionDTO.getTxid());
        } else {
            log.info("sent tx {} failed, reason {}", transactionDTO.getTxid(), transactionDTO.getError().getMessage());
        }
    }

    protected abstract boolean sendRawTransaction(String rawTx, TransactionDTO transactionDTO);

    protected abstract void parseSendersAndReceivers(TransactionDTO transactionDTO, RawTx rawTransaction,
                                                     Function<String, Boolean> assetFilter, Function<String, Boolean> addressFilter);
}
