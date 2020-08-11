package com.tokimi.chain.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "chn_sweep_flows")
public class SweepFlow extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "address")
    private String address;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "gas_token_id", nullable = false)
    private Long gasTokenId;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "txid")
    private String txid;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "status_str")
    private String statusStr;

    @Column(name = "memo", nullable = false)
    private String memo;
}
