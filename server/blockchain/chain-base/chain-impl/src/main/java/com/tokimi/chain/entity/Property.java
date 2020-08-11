package com.tokimi.chain.entity;

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
@Table(name = "chn_crypto_token_properties")
public class Property extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "`key`")
    private String key;

    private String comment;

    @Column(name = "`decimal`")
    private Integer decimal;
}