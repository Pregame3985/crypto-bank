package com.mcb.address.manager.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author william
 */
@Getter
@Setter
@Entity
@Table(name = "user_address")
public class UserAddress extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "coin_type")
    private Integer tokenId;

    @Column(name = "slip44_coin_type")
    private Integer slip44CoinType;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "key_imported")
    private Boolean keyImported;

    @Column(name = "address")
    private String address;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "user_memo")
    private String memo;
}