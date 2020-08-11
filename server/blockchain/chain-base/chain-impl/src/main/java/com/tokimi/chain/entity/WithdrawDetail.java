package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * @author william
 */
@Getter
@Setter
@Entity(name = "chn_withdraw_details")
public class WithdrawDetail extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_id", nullable = false, updatable = false)
    private Long tokenId;

    @Column(name = "token_type")
    private Integer tokenType;

    @Column(name = "type", nullable = false)
    private Integer type;

    @Column(name = "memo", nullable = false)
    private String memo;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "txid")
    private String txid;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "utxo_id")
    private Long utxoId;

    @Column(name = "address")
    private String address;

    @Column(name = "direction")
    private Integer direction;

    @Column(name = "status")
    private Integer status;

    @Column(name = "status_str")
    private String statusStr;

    @Column(name = "flow_request_id")
    private Long withdrawRequestId;

    @ManyToOne
    @JoinColumn(name = "flow_id")
    private WithdrawFlow withdrawFlow;
}
