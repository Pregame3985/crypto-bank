package com.tokimi.chain.rpc.model.eth.request;

import com.tokimi.chain.rpc.model.eth.response.GasPriceResponse;
import com.tokimi.common.network.rpc.model.RpcRequest20;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GasPriceRequest extends RpcRequest20<GasPriceResponse> {

    public GasPriceRequest() {
        super("eth_gasPrice", null, GasPriceResponse.class);
    }

}
