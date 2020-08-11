package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum ScriptType {

    UNKNOWN((byte) -1, "UNKNOWN"),
    P2PKH((byte) 0, "P2PKH"),
    P2SH((byte) 8, "P2SH"),
    P2SHP2WPKH((byte) 16, "P2SH-P2WPKH"),
    OMNI((byte) 1, "OMNI");

    @Getter
    @JsonProperty
    private byte value;

    @Getter
    private String name;

    ScriptType(byte value, String name) {
        this.value = value;
        this.name = name;
    }
}