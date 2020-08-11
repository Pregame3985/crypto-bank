package com.tokimi.chain.rpc.model.eth.request;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;
import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GetTransactionByHashRequest extends RpcRequest20<GetTransactionResponse> {

    public GetTransactionByHashRequest(String hash) {
        super("eth_getTransactionByHash", Lists.newArrayList(hash), GetTransactionResponse.class);
    }

}
