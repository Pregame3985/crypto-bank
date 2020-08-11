package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum TokenType {

    UNKNOWN(-1, "UNKNOWN"),
    NATIVE(0, "NATIVE"), // native token
    TOKEN(1, "TOKEN"); // guest token that supported by native chain

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    TokenType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static TokenType forValue(Integer value) {
        for (TokenType tokenType : TokenType.values()) {
            if (null != value && tokenType.value.compareTo(value) == 0) {
                return tokenType;
            }
        }
        return UNKNOWN;
    }
}