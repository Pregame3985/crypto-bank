package com.tokimi.common.chain.service.tx;

import com.tokimi.common.chain.model.ChainTransaction;
import com.tokimi.common.chain.model.RawTx;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.model.WithdrawRequestDTO;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author william
 */
public interface TransactionService {

    // Integer getTransactionType();

    TransactionDTO get(String txid);

    TransactionDTO get(String txid, Function<String, Boolean> addressFilter);

    TransactionDTO get(String txid, Function<String, Boolean> addressFilter, Function<String, Boolean> assetFilter);

    TransactionDTO parseTx(Supplier<ChainTransaction<? extends RawTx>> txFunc,
                           Function<String, Boolean> assetFilter,
                           Function<String, Boolean> addressFilter);

    @Deprecated
    TransactionDTO assemble(Long tokenId, Integer tokenType, Integer txType, List<WithdrawRequestDTO> requests);

    TransactionDTO assemble(WithdrawRequestDTO dto);

    void send(TransactionDTO transactionDTO);
}
