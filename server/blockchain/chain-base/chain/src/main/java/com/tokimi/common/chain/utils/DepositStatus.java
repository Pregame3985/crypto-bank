package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum DepositStatus {

    PENDING(0, "PENDING"),
    VERIFIED(2, "VERIFIED"),
    CONFIRMED(4, "CONFIRMED"),
    SUCCESS(5, "SUCCESS"),
    FAILED(102, "FAILED");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    DepositStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static DepositStatus forValue(Integer value) {
        for (DepositStatus depositStatus : DepositStatus.values()) {
            if (null != value && depositStatus.value.compareTo(value) == 0) {
                return depositStatus;
            }
        }
        return PENDING;
    }
}