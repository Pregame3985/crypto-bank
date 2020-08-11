package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum TxStatus {

    UNKNOWN(-99, "UNKNOWN"),
    FAILED(-1, "FAILED"),
    PENDING(0, "PENDING"),
    SUCCESSFUL(1, "SUCCESSFUL");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    TxStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static TxStatus forValue(Integer value) {
        for (TxStatus txStatus : TxStatus.values()) {
            if (null != value && txStatus.value.compareTo(value) == 0) {
                return txStatus;
            }
        }
        return null;
    }
}