package com.tokimi.chain.rpc.model.eth.request;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.GetBalanceResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;
import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GetBalanceRequest extends RpcRequest20<GetBalanceResponse> {

    public GetBalanceRequest(String address) {
        super("eth_getBalance", Lists.newArrayList(address, "latest"), GetBalanceResponse.class);
    }

}
