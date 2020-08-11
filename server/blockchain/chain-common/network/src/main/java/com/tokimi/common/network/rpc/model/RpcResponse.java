package com.tokimi.common.network.rpc.model;

import java.io.Serializable;

import com.tokimi.common.ErrorDTO;
import com.tokimi.common.network.ChainError;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RpcResponse<ID extends Serializable, T extends Object> {

    protected ID id;

    protected T result;

    @JsonProperty("error")
    protected ChainError chainError;

    @JsonIgnore
    protected ErrorDTO fullError;

    @JsonIgnore
    public boolean isSuccess() {
        return null == this.chainError && null == getFullError();
    }
}
