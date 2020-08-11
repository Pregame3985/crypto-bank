package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum NetworkType {

    UNKNOWN((byte) -1, "UNKNOWN"),
    MAIN((byte) 0, "MAIN"),
    TEST((byte) 1, "TEST");

    @Getter
    @JsonProperty
    private byte value;

    @Getter
    private String name;

    NetworkType(byte value, String name) {
        this.value = value;
        this.name = name;
    }
}