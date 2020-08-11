package com.tokimi.common.network.http.model;

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
public abstract class HttpRequest<T extends HttpResponse> {

    @JsonIgnore
    protected Class<T> responseType;
    private String baseUrl;
    private String uri;
}
