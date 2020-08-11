package com.tokimi.chain.rpc.model.eth.response;

import com.tokimi.common.network.rpc.model.RpcResponse20;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GetTransactionReceiptResponse extends RpcResponse20<Transaction> {
}
