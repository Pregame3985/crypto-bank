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
public class RpcRequest10<T extends RpcResponse10> extends RpcRequest<String, T> {

    private static AtomicLong nextId = new AtomicLong(0);

    public RpcRequest10(String method, List<Object> params, Class<T> type) {
        super(method, params, type);
        this.id = String.valueOf(nextId.getAndIncrement());
        this.jsonrpc = "1.0";
    }
}
