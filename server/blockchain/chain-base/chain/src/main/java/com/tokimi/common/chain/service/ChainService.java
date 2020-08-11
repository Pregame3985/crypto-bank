package com.tokimi.common.chain.service;

import com.tokimi.common.network.rpc.JsonRpcAgent;

/**
 * @author william
 */
public interface ChainService {

    Long getAssetId();

    Integer getNetwork();

    Integer getDepositConfirmations();

    Integer getWithdrawConfirmations();

    Long getBlockHeight();

    JsonRpcAgent getJsonRpcAgent();
}
