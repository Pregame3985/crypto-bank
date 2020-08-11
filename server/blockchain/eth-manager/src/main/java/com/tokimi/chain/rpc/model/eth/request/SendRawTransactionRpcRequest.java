package com.tokimi.chain.rpc.model.eth.request;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.SendRawTransactionRpcResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;

/**
 * @author william
 */
public class SendRawTransactionRpcRequest extends RpcRequest20<SendRawTransactionRpcResponse> {

    public SendRawTransactionRpcRequest(String rawTx) {
        super("eth_sendRawTransaction", Lists.newArrayList(rawTx), SendRawTransactionRpcResponse.class);
    }
}