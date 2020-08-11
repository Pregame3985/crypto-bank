package com.tokimi.common.network.http;

import static com.tokimi.common.ErrorConstants.RPC_NODE_NOT_PREPARED;
import static com.tokimi.common.ErrorConstants.RPC_REQUEST_ERROR;

import java.io.IOException;
import java.net.ConnectException;

import com.tokimi.common.ErrorConstants;
import com.tokimi.common.ErrorDTO;
import com.tokimi.common.Utils;
import com.tokimi.common.network.http.model.HttpRequest;
import com.tokimi.common.network.http.model.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;


/**
 * @author william
 */
@Slf4j
public class DefaultHttpAgent implements HttpAgent {

    private ObjectMapper objectMapper;

    private WebClient networkNode;

    private WebClient signNode;

    public DefaultHttpAgent(WebClient networkNode, WebClient signNode, ObjectMapper objectMapper) {
        this.networkNode = networkNode;
        this.signNode = signNode;
        this.objectMapper = objectMapper;
    }

    @Override
    public <RQ extends HttpRequest<RS>, RS extends HttpResponse> RS send(RQ request) {
        return doHttpRequest(request, true);
    }

    private <RQ extends HttpRequest<RS>, RS extends HttpResponse> RS doHttpRequest(RQ request, boolean handleError) {
        Class<RS> responseType = request.getResponseType();

        WebClient.Builder networkBuilder = WebClient.builder().baseUrl(request.getBaseUrl());

        Mono<RS> mono = networkBuilder.build().post()
                .uri(request.getUri())
                .syncBody(request)
                .retrieve()
                .bodyToMono(responseType);

        if (handleError) {
            return mono.onErrorResume(throwable -> Mono.just(extractErrorMessage(responseType, throwable))).block();
        } else {
            return mono.block();
        }
    }

    private <RS extends HttpResponse> RS extractErrorMessage(Class<RS> responseType, Throwable throwable) {

        RS response;

        if (throwable instanceof ConnectException) {
            response = handleDefaultError(throwable, responseType, RPC_NODE_NOT_PREPARED);
        } else if (throwable instanceof WebClientResponseException) {
            response = handleWebClientError((WebClientResponseException) throwable, responseType);
        } else {
            response = handleDefaultError(throwable, responseType, RPC_REQUEST_ERROR);
        }
        return response;
    }

    private <RS extends HttpResponse> RS handleWebClientError(WebClientResponseException e, Class<RS> responseType) {

        RS response;

        String responseBody = e.getResponseBodyAsString();

        if (!Utils.isEmpty(responseBody) && responseBody.startsWith("{") && (e.getStatusCode().is4xxClientError() || e.getStatusCode().is5xxServerError())) {

            log.error("http error status code : {}, message : {}", e.getRawStatusCode(), e.getMessage());

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

    private <RS extends HttpResponse> RS handleDefaultError(Throwable throwable, Class<RS> responseType, ErrorConstants error) {

        RS response = null;

        try {
            response = responseType.newInstance();
            response.setError(new ErrorDTO(error, throwable.getMessage()));
        } catch (InstantiationException | IllegalAccessException e) {
            log.error("create response failed, message {}", e.getMessage());
        }

        return response;
    }
}
