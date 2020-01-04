package com.mcb.address.manager.util;

public enum WalletTypeEnum {
    COLD(10),
    EGRESS(20),
    INGRESS(30);

    private final Integer value;

    WalletTypeEnum(final Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
