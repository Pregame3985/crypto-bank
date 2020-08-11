package com.tokimi.common.network.http.model;

import com.tokimi.common.ErrorDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class HttpResponse {

    private ErrorDTO error;

    @JsonIgnore
    public boolean isSuccess() {
        return null == this.error;
    }
}
