package com.tokimi.common.chain.model;

import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.Utils;

import lombok.Getter;

/**
 * @author william
 */
public abstract class AbstractChainTxWrapper<T extends RawTx> implements ChainTransaction<T> {

    @Getter
    private T tx;

    @Getter
    private final ChainService chainService;

    @Getter
    private final BlockService blockService;

    protected AbstractChainTxWrapper(ChainService chainService, BlockService blockService, T tx) {
        this.chainService = chainService;
        this.blockService = blockService;
        this.tx = tx;
    }

    @Override
    public String getTxid() {
        return this.tx.getTxid();
    }

    @Override
    public Long getConfirmation() {
        return null != getBlockHeight() && null != chainService.getBlockHeight()
                ? chainService.getBlockHeight() - getBlockHeight() + 1
                : 0;
    }

    @Override
    public String getBlockHash() {

        String blockHash = null;

        String txBlockHash = this.tx.getBlockhash();

        if (!Utils.isEmpty(txBlockHash)) {
            blockHash = txBlockHash;
        } else {
            if (isValid()) {
                BlockDTO blockDTO = this.blockService.getBlock(getBlockHeight());
                blockHash = blockDTO.getHash();
            }
        }

        return blockHash;
    }

    @Override
    public Long getBlockHeight() {
        return null != this.tx.getHeight() ? this.tx.getHeight() : 0;
    }

    @Override
    public boolean isValid() {
        return this.getTx().isValid();
    }

    // TODO: check blockhash exist or not
    @Override
    public boolean isConfirmed(Long toBeConfirm) {
        return getConfirmation() >= toBeConfirm;
    }

    @Override
    public String getMessage() {
        return this.tx.getMessage();
    }
}