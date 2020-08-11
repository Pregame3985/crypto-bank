package com.tokimi.common.chain.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeWalletDTO {

    private Long userId;

    private Long tokenId;

    private Integer type;

    private String address;

    private BigDecimal balance;

    private Integer mineTokenId;

    private BigDecimal mineFeeBalance;

    private BigDecimal insufficientLimit;

    private BigDecimal withdrawLimit;
}
