package com.tokimi.common.network.rpc;

import java.io.Serializable;
import java.util.Map;

import com.tokimi.common.network.rpc.model.RpcRequest;
import com.tokimi.common.network.rpc.model.RpcResponse;

import reactor.core.publisher.Mono;

/**
 * @author william
 */
public interface JsonRpcAgent {

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS sendToSigning(RQ request);

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS sendToNetwork(RQ request);

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS unsafeSendToSigning(RQ request);

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS unsafeSendToNetwork(RQ request);

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> send(RQ request);

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> send(RQ request, Map<String, String> headers);

    <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> unsafeSend(RQ request);
}
