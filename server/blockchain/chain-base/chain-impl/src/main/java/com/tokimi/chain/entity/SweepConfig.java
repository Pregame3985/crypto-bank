package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author william
 */
@Getter
@Setter
//@Entity
//@Table(name = "sweep_configs")
public class SweepConfig extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "min_sweep_amount")
    private BigDecimal minSweepAmount;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "fee_token_id")
    private Long feeTokenId;

    @Column(name = "fee_unit_price")
    private BigDecimal feeUnitPrice;

    @Column(name = "reserve_times")
    private Integer reserveTimes;

    @Column(name = "charge_times")
    private Integer chargeTimes;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}