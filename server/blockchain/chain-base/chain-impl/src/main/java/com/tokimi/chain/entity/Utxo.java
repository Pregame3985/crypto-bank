package com.tokimi.chain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "chn_utxos")
public class Utxo extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "`index`")
    private Integer index;

    private Long height;

    private String txid;

    @Column(name = "spent_height")
    private Long spentHeight;

    @Column(name = "spent_txid")
    private String spentTxid;

    // @ManyToOne
    // @JoinColumn(name = "property_id")
    // private Property property;

    private BigDecimal amount;

    private String address;

    private String account;

    @Column(name = "script_type")
    private String scriptType;

    @Column(name = "script")
    private String script;

    private Boolean locking;

    private Boolean spendable;

    private Boolean synced;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "deleted")
    private boolean deleted = false;
}