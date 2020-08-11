package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum SweepStatus {

    UNKNOWN(-99, "UNKNOWN"),
    FAILED(-1, "FAILED"),
    PENDING(0, "PENDING"),
    SUCCESS(1, "SUCCESS");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    SweepStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static SweepStatus forValue(Integer value) {
        for (SweepStatus sweepType : SweepStatus.values()) {
            if (null != value && sweepType.value.compareTo(value) == 0) {
                return sweepType;
            }
        }
        return null;
    }
}