package com.tokimi.common.chain.model;

import com.tokimi.common.chain.utils.TokenType;

/**
 * @author william
 */
public interface ChainTransaction<T extends RawTx> {

    T getTx();

    String getTxid();

    Long getConfirmation();

    String getBlockHash();

    Long getBlockHeight();

    boolean needFetch();

    boolean isValid();

    boolean isConfirmed(Long toBeConfirm);

    String getMessage();

    TokenType getTokenType();
}
