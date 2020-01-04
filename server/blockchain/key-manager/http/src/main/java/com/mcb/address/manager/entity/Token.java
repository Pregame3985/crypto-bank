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
@Table(name = "fvirtualcointype")
public class Token extends AbstractEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fId", unique = true, nullable = false)
    private Integer id;

    @Column(name = "fstatus")
    private Integer status;

    @Column(name = "fname")
    private String name;

    @Column(name = "fShortName")
    private String shortName;

    @Column(name = "fSymbol")
    private String symbol;

    @Column(name = "parentCoinType")
    private Integer parentTokenId;

    @Column(name = "ftype")
    private Integer type;

    @Column(name = "confirmations")
    private Integer depositConfirmations;

    @Column(name = "withdraw_confirmations")
    private Integer withdrawConfirmations;

    @Column(name = "tx_base_url")
    private String txBaseUrl;

    @Column(name = "address_base_url")
    private String addressBaseUrl;

    //地址校验不再使用数据库正则配置，全部通过coding来实现 by conanliu 2019-09-12
//    @Column(name = "faddressMatch")
//    private String regex;
}