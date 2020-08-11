package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum RequestStatus {

    STATUS_REJECT(0, "STATUS_REJECT"),
    STATUS_APPROVE(1, "STATUS_APPROVE"),
    STATUS_PENDING(2, "STATUS_PENDING"),
    STATUS_FAILED(3, "STATUS_FAIL");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    RequestStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static RequestStatus forValue(Integer value) {
        for (RequestStatus requestStatus : RequestStatus.values()) {
            if (null != value && requestStatus.value.compareTo(value) == 0) {
                return requestStatus;
            }
        }
        return STATUS_PENDING;
    }
}