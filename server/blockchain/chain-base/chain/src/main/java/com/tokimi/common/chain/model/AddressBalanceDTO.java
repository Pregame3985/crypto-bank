package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author william
 */
@Getter
@Setter
@ToString
public class AddressBalanceDTO {

    private Long tokenId;

    private String balance;

    private Long feeTokenId;

    private String feeBalance;

    private String address;

    private Long userId;
}
