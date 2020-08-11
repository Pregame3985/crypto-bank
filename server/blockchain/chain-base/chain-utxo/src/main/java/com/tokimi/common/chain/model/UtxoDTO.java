package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author william
 */
@Setter
@Getter
public class UtxoDTO {

    private Long id;

    private Long tokenId;

    private Integer index;

    private Long height;

    private String txid;

    private Long spentHeight;

    private String spentTxid;

    private BigDecimal amount;

    private String address;

    private Boolean locking;

    private Boolean spendable;

    private Boolean synced;
}