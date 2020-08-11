package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum AddressType {

    UNKNOWN((byte) -1, "UNKNOWN"),
    BASE58((byte) 0, "BASE58"),
    CASHADDR((byte) 8, "CASHADDR");

    @Getter
    @JsonProperty
    private byte value;

    @Getter
    private String name;

    AddressType(byte value, String name) {
        this.value = value;
        this.name = name;
    }
}