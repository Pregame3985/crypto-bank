package com.tokimi.common.network.http;

import com.tokimi.common.network.http.model.HttpRequest;
import com.tokimi.common.network.http.model.HttpResponse;

/**
 * @author william
 */
public interface HttpAgent {

    <RQ extends HttpRequest<RS>, RS extends HttpResponse> RS send(RQ request);
}
