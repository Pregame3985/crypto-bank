package com.tokimi.chain.model;

import com.tokimi.chain.rpc.model.eth.response.Transaction;
import com.tokimi.common.Utils;
import com.tokimi.common.chain.model.AbstractChainTxWrapper;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sync.BlockService;
import com.tokimi.common.chain.utils.BinaryUtils;
import com.tokimi.common.chain.utils.TokenType;
import org.springframework.util.StringUtils;

/**
 * @author william
 */
public class ChainTxWrapper extends AbstractChainTxWrapper<Transaction> {

    public ChainTxWrapper(ChainService chainService, BlockService blockService, Transaction tx) {
        super(chainService, blockService, tx);
    }

    @Override
    public boolean needFetch() {
        return false;
    }

    @Override
    public TokenType getTokenType() {

        if (!Utils.isEmpty(this.getTx().getInput())) {
            return this.getTx().getInput().equals("0x") ? TokenType.NATIVE : TokenType.TOKEN;
        }
        return TokenType.UNKNOWN;
    }

    @Override
    public Long getBlockHeight() {
        if (!StringUtils.isEmpty(this.getTx().getBlockNumber())) {
            return BinaryUtils.hexToLong(this.getTx().getBlockNumber());
        } else {
            return 0L;
        }
    }

    @Override
    public boolean isValid() {
        return this.getTx().isValid();
    }
}