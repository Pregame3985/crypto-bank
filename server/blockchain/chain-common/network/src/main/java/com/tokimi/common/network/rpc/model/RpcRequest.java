package com.tokimi.common.network.rpc.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RpcRequest<ID extends Serializable, RS extends RpcResponse> {

    protected String jsonrpc;
    protected String method;
    protected List<Object> params;
    protected ID id;
    @JsonIgnore
    protected Class<RS> responseType;

    RpcRequest(String method, List<Object> params, Class<RS> type) {
        this.method = method;
        this.params = params;
        this.responseType = type;
    }
}
