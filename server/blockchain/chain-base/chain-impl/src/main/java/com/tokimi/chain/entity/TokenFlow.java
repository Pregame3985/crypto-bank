package com.tokimi.chain.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@MappedSuperclass
public abstract class TokenFlow extends AbstractPersistableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    @Column(name = "token_type", nullable = false)
    private Integer tokenType;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "txid", updatable = false)
    private String txid;

    @Column(name = "status")
    private Integer status;
}
