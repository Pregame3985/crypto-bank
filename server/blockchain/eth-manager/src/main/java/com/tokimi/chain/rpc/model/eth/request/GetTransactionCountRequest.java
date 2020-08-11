package com.tokimi.chain.rpc.model.eth.request;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.GetTransactionCountResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;
import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GetTransactionCountRequest extends RpcRequest20<GetTransactionCountResponse> {

    public GetTransactionCountRequest(String address) {
        super("eth_getTransactionCount", Lists.newArrayList(address, "latest"),
                GetTransactionCountResponse.class);
    }

}
