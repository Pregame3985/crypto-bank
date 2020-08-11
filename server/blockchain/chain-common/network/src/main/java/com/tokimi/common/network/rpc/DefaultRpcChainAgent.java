package com.tokimi.common.network.rpc;

import static com.tokimi.common.ErrorConstants.RPC_NODE_NOT_PREPARED;
import static com.tokimi.common.ErrorConstants.RPC_REQUEST_ERROR;
import static com.tokimi.common.ErrorConstants.RPC_SERVER_RESET_CONNECTION;

import java.io.IOException;
import java.io.Serializable;
import java.net.ConnectException;
import java.time.Duration;
import java.util.Map;

import com.tokimi.common.network.rpc.model.RpcRequest;
import com.tokimi.common.ErrorConstants;
import com.tokimi.common.ErrorDTO;
import com.tokimi.common.Utils;
import com.tokimi.common.network.rpc.model.RpcResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author william
 */
@Slf4j
public class DefaultRpcChainAgent implements JsonRpcAgent {

    private ObjectMapper objectMapper;

    private WebClient networkNode;

    private WebClient signNode;

    private int retryTimes;

    public DefaultRpcChainAgent(WebClient networkNode, WebClient signNode, ObjectMapper objectMapper) {
        this.networkNode = networkNode;
        this.signNode = signNode;
        this.objectMapper = objectMapper;
    }

    public DefaultRpcChainAgent(WebClient networkNode, WebClient signNode, ObjectMapper objectMapper, Integer retryTimes) {
        this(networkNode, signNode, objectMapper);
        this.retryTimes = (null != retryTimes) ? retryTimes : 5;
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS sendToSigning(RQ request) {
        return doBlockRpcRequest(this.signNode, request, true);
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS sendToNetwork(RQ request) {
        return doBlockRpcRequest(this.networkNode, request, true);
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS unsafeSendToSigning(RQ request) {
        return doBlockRpcRequest(this.signNode, request, false);
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS unsafeSendToNetwork(RQ request) {
        return doBlockRpcRequest(this.networkNode, request, false);
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> send(RQ request) {
        return doRpcRequest(this.networkNode, request, true, null);
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> unsafeSend(RQ request) {
        return null;
    }

    @Override
    public <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> send(RQ request, Map<String, String> headers) {
        return doRpcRequest(this.networkNode, request, true, headers);
    }

    private <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> Mono<RS> doRpcRequest(
            WebClient node,
            RQ request,
            boolean handleError,
            Map<String, String> headers) {
        Class<RS> responseType = request.getResponseType();

        WebClient.RequestHeadersSpec<?> requestHeadersSpec = node.post()
                .syncBody(request);

        if (null != headers) {
            for (String key : headers.keySet())
                requestHeadersSpec = requestHeadersSpec.header(key, headers.get(key));
        }

        Mono<RS> mono = requestHeadersSpec
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(companion -> companion
                        .log()
                        .zipWith(Flux.range(0, retryTimes + 1), (error, index) -> {
                            if (index < retryTimes && !(error instanceof WebClientResponseException)) {
                                return index;
                            } else {
                                throw Exceptions.propagate(error);
                            }
                        })
                        .flatMap(index -> {
                            log.error("  __ __  ______  ______  ____       ____   ____    __        ___  ____   ____   ___   ____   ");
                            log.error(" |  |  ||      ||      ||    \\     |    \\ |    \\  /  ]      /  _]|    \\ |    \\ /   \\ |    \\  ");
                            log.error(" |  |  ||      ||      ||  o  )    |  D  )|  o  )/  /      /  [_ |  D  )|  D  )     ||  D  ) ");
                            log.error(" |  _  ||_|  |_||_|  |_||   _/     |    / |   _//  /      |    _]|    / |    /|  O  ||    /  ");
                            log.error(" |  |  |  |  |    |  |  |  |       |    \\ |  | /   \\_     |   [_ |    \\ |    \\|     ||    \\  ");
                            log.error(" |  |  |  |  |    |  |  |  |       |  .  \\|  | \\     |    |     ||  .  \\|  .  \\     ||  .  \\ ");
                            log.error(" |__|__|  |__|    |__|  |__|       |__|\\_||__|  \\____|    |_____||__|\\_||__|\\_|\\___/ |__|\\_| ");
                            log.error("request method : {}", request.getMethod());
                            log.error("request params : {}", request.getParams());
                            log.error("request retry times {}", index);
                            return Mono.delay(Duration.ofSeconds(index * 2 + 1));
                        })
                );

        if (handleError) {
            return mono.onErrorResume(throwable -> {

                log.error("  __ __  ______  ______  ____       ____   ____    __        ___  ____   ____   ___   ____   ");
                log.error(" |  |  ||      ||      ||    \\     |    \\ |    \\  /  ]      /  _]|    \\ |    \\ /   \\ |    \\  ");
                log.error(" |  |  ||      ||      ||  o  )    |  D  )|  o  )/  /      /  [_ |  D  )|  D  )     ||  D  ) ");
                log.error(" |  _  ||_|  |_||_|  |_||   _/     |    / |   _//  /      |    _]|    / |    /|  O  ||    /  ");
                log.error(" |  |  |  |  |    |  |  |  |       |    \\ |  | /   \\_     |   [_ |    \\ |    \\|     ||    \\  ");
                log.error(" |  |  |  |  |    |  |  |  |       |  .  \\|  | \\     |    |     ||  .  \\|  .  \\     ||  .  \\ ");
                log.error(" |__|__|  |__|    |__|  |__|       |__|\\_||__|  \\____|    |_____||__|\\_||__|\\_|\\___/ |__|\\_| ");
                log.error("request method : {}", request.getMethod());
                log.error("request params : {}", request.getParams());
                log.error("throwable : {}", throwable.getMessage());
                log.error("request final error");
                return Mono.fromSupplier(() -> extractErrorMessage(responseType, throwable));
            });
        } else {
            return mono;
        }
    }

    private <ID extends Serializable, RQ extends RpcRequest<ID, RS>, RS extends RpcResponse> RS doBlockRpcRequest(WebClient node, RQ request, boolean handleError) {
        return doRpcRequest(node, request, handleError, null).block();
    }

    private <RS extends RpcResponse> RS extractErrorMessage(Class<RS> responseType, Throwable throwable) {

        RS response;

        if (throwable instanceof ConnectException) {
            response = handleDefaultError(throwable, responseType, RPC_NODE_NOT_PREPARED);
        } else if (throwable instanceof IOException) {
            response = handleDefaultError(throwable, responseType, RPC_SERVER_RESET_CONNECTION);
        } else if (throwable instanceof WebClientResponseException) {
            response = handleWebClientError((WebClientResponseException) throwable, responseType);
        } else {
            response = handleDefaultError(throwable, responseType, RPC_REQUEST_ERROR);
        }
        return response;
    }

    private <RS extends RpcResponse> RS handleWebClientError(WebClientResponseException e, Class<RS> responseType) {

        RS response;

        String responseBody = e.getResponseBodyAsString();

        if (!Utils.isEmpty(responseBody) && responseBody.startsWith("{") && (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError())) {

            log.error("http error status code : {}, message : {}, response body : {}", e.getRawStatusCode(), e.getMessage(), responseBody);

            try {
                response = objectMapper.readValue(responseBody, responseType);
            } catch (IOException ioe) {
                response = handleDefaultError(e, responseType, RPC_REQUEST_ERROR);
            }
        } else {
            log.info("response body is {}", e.getMessage());
            response = handleDefaultError(e, responseType, RPC_REQUEST_ERROR);
        }

        return response;
    }

    private <RS extends RpcResponse> RS handleDefaultError(Throwable throwable, Class<RS> responseType, ErrorConstants error) {

        RS response = null;

        try {
            response = responseType.newInstance();
            response.setFullError(new ErrorDTO(error, throwable.getMessage()));
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("create response failed, message {}", e.getMessage());
        }

        return response;
    }
}
