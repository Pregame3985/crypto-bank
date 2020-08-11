package com.tokimi.common.network.rpc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author william
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(
        ignoreUnknown = true
)
public class RpcRequest20<T extends RpcResponse20> extends RpcRequest<Long, T> {

    private static AtomicLong nextId = new AtomicLong(0);

    public RpcRequest20(String method, List<Object> params, Class<T> type) {
        super(method, params, type);
        this.id = nextId.getAndIncrement();
        this.jsonrpc = "2.0";
    }
}
