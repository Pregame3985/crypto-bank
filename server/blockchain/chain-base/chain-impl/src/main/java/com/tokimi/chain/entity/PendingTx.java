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
@Table(name = "chn_pending_txs")
public class PendingTx extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "token_type")
    private Integer tokenType;

    @Column(nullable = false)
    private Long height;

    @Column(name = "block_hash", nullable = false)
    private String blockHash;

    @Column(name = "txid", nullable = false)
    private String txid;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "address")
    private String address;

    @Column(name = "reorg")
    private Boolean reorg;

    @Column(name = "processed")
    private Boolean processed;
}