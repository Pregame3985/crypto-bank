package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "chn_registries")
public class Registry extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "app_id", unique = true)
    private String appId;

    @Column(name = "callback_url")
    private String callbackUrl;
}