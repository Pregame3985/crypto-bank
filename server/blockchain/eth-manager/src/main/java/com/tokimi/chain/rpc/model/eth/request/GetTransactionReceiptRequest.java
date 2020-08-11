package com.tokimi.chain.rpc.model.eth.request;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionReceiptResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;
import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GetTransactionReceiptRequest extends RpcRequest20<GetTransactionReceiptResponse> {

    public GetTransactionReceiptRequest(String hash) {
        super("eth_getTransactionReceipt", Lists.newArrayList(hash), GetTransactionReceiptResponse.class);
    }

}
