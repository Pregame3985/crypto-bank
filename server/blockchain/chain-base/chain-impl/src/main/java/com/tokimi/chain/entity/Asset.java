package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.math.BigDecimal;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "chn_assets")
public class Asset extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "shortName")
    private String shortName;

    @Column(name = "unique_id")
    private String uniqueId;

    @Column(name = "symbol")
    private String symbol;

    @Column(name = "type")
    private Integer type;

    @Column(name = "status")
    private Integer status;

    @Column(name = "status_str")
    private String statusStr;

    @Column(name = "allowing_sync")
    private Boolean allowingSnyc;

    @Column(name = "allowing_deposit")
    private Boolean allowingDeposit;

    @Column(name = "allowing_withdraw")
    private Boolean allowingWithdraw;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(name = "deposit_confirmations")
    private Integer depositConfirmations;

    @Column(name = "withdraw_confirmations")
    private Integer withdrawConfirmations;

    @Column(name = "withdraw_charge")
    private BigDecimal withdrawCharge;

    @Column(name = "tx_base_url")
    private String txBaseUrl;

    @Column(name = "address_base_url")
    private String addressBaseUrl;

    @Version
    @Column(name = "version")
    private Long version;
}