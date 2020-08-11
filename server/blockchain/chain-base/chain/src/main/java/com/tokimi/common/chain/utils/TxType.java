package com.tokimi.common.chain.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

/**
 * @author william
 */
public enum TxType {

    UNKNOWN(0, "UNKNOWN"),
    WITHDRAW(1, "WITHDRAW"),
    SWEEP(2, "SWEEP"),
    CHARGE(3, "CHARGE"),
    DEPOSIT(4, "DEPOSIT"),
    CHANGE(5, "CHANGE"),
    FEE(10, "FEE"),
    MANUAL_DEPOSIT(11, "MANUAL_DEPOSIT"),
    AUTO_DEPOSIT(12, "AUTO_DEPOSIT"),
    MANUAL_WITHDRAW(41, "MANUAL_WITHDRAW"),
    REFUND(100, "REFUND"),
    MERGE(101, "MERGE"),                // merge small utxo single big one, like btc
    BREAK_DOWN(102, "BREAK_DOWN"),       // break down big utxo to small one, like omni
    ;

    @Getter
    @JsonProperty
    private final Integer value;

    @Getter
    private final String name;

    TxType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static TxType forValue(Integer value) {
        for (TxType txType : TxType.values()) {
            if (null != value && txType.value.compareTo(value) == 0) {
                return txType;
            }
        }
        return UNKNOWN;
    }
}