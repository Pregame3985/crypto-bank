package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * @author william
 */
public enum TrackStatus {

    NOOP(-1, "NOOP"),       // Track without operation
    UNKNOWN(0, "UNKNOWN"),
    RECEIVE(1, "RECEIVE"),
    SENT(2, "SENT"),
    VERIFY(3, "VERIFY"),
    //    CONFIRMED(4, "CONFIRMED"),
    SUCCESS(5, "SUCCESS"),
    FAILED(101, "FAILED");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    TrackStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static TrackStatus forValue(Integer value) {
        for (TrackStatus trackStatus : TrackStatus.values()) {
            if (null != value && trackStatus.value.compareTo(value) == 0) {
                return trackStatus;
            }
        }
        return null;
    }
}