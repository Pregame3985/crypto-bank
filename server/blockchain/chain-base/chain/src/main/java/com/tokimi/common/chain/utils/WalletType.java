package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author william
 */
public enum WalletType {

    UNKNOWN(0, "UNKNOWN"),
    COLD(10, "COLD"),
    EGRESS(20, "EGRESS"),
    INGRESS(30, "INGRESS");

    @Getter
    @JsonProperty
    private Integer value;

    @Getter
    private String name;

    WalletType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static WalletType forValue(Integer value) {
        for (WalletType walletType : WalletType.values()) {
            if (null != value && walletType.value.compareTo(value) == 0) {
                return walletType;
            }
        }
        return UNKNOWN;
    }
}