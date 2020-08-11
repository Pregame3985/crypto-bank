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
public class GetBlockByHashRequest extends RpcRequest20<GetBlockResponse> {

    public GetBlockByHashRequest(String hash) {
        super("eth_getBlockByHash", Lists.newArrayList(hash, true), GetBlockResponse.class);
    }

}
