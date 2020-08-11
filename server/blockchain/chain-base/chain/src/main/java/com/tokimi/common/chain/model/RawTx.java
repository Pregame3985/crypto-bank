package com.tokimi.common.chain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tokimi.common.chain.utils.TokenType;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class RawTx {

    private Long version;

    private String type;

    private String txid;

    private Long confirmations;

    private Long height;

    private String blockhash;

    private TokenType tokenType = TokenType.UNKNOWN;

    public abstract boolean isValid();

    public abstract String getMessage();
}
