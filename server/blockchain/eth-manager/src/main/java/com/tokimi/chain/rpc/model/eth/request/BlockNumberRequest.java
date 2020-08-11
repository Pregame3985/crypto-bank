package com.tokimi.chain.rpc.model.eth.request;

import com.tokimi.chain.rpc.model.eth.response.BlockNumberResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class BlockNumberRequest extends RpcRequest20<BlockNumberResponse> {

    public BlockNumberRequest() {
        super("eth_blockNumber", null, BlockNumberResponse.class);
    }

}
