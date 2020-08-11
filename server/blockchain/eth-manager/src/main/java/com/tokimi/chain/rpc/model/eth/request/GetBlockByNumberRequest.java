package com.tokimi.chain.rpc.model.eth.request;

import com.google.common.collect.Lists;
import com.tokimi.chain.rpc.model.eth.response.GetBlockResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;
import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GetBlockByNumberRequest extends RpcRequest20<GetBlockResponse> {

    public GetBlockByNumberRequest(Long number) {
        super("eth_getBlockByNumber", Lists.newArrayList("0x" + Long.toHexString(number), true),
                GetBlockResponse.class);
    }

}
