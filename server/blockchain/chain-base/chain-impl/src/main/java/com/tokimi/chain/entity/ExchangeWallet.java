package com.tokimi.chain.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Entity
@Getter
@Setter
@Table(name = "chn_exchange_wallets")
public class ExchangeWallet extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "address")
    private String address;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "salt")
    private String salt;

    @Column(name = "type")
    private Integer type;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "insufficient_limit")
    private BigDecimal insufficientLimit;

    @Column(name = "withdraw_limit")
    private BigDecimal withdrawLimit;

    @Column(name = "gas_token_id")
    private Long gasTokenId;

    @Column(name = "gas_balance")
    private BigDecimal gasBalance;

    @Version
    @Column(name = "version")
    private Long version;
}
