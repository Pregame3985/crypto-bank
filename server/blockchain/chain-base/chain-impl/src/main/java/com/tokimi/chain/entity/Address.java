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
@Entity
@Getter
@Setter
@Table(name = "chn_addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "ext_user_id")
    private String extUserId;

    @Column(name = "app_id")
    private String appId;

    @Column(name = "secret_id")
    private String secretId;

    @Column(name = "assert_id")
    private Long tokenId;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "address")
    private String address;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "salt")
    private String salt;

    @Column(name = "hot_wallet")
    private Boolean hotWallet;
}