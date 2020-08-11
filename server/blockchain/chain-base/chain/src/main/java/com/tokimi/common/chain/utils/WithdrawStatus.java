package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum WithdrawStatus {

    PENDING(0, "PENDING"),
    SENT(1, "SENT"),
    VERIFIED(2, "VERIFIED"),
    CONFIRMED(4, "CONFIRMED"),
    SUCCESS(5, "SUCCESS"),
    FAILED(102, "FAILED"),
    REVIEWING(10000, "REVIEWING");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    WithdrawStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static WithdrawStatus forValue(Integer value) {
        for (WithdrawStatus withdrawStatus : WithdrawStatus.values()) {
            if (null != value && withdrawStatus.value.compareTo(value) == 0) {
                return withdrawStatus;
            }
        }
        return PENDING;
    }
}