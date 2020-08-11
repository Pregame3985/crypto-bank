package com.tokimi.chain.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;

/**
 * @author william
 */
@Getter
@Setter
@Entity(name = "chn_withdraw_requests")
public class WithdrawRequest extends AbstractEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "token_id")
    private Long tokenId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "memo", updatable = false)
    private String memo;

    @Column(name = "to_address", updatable = false)
    private String toAddress;

    @Column(name = "from_address", updatable = false)
    private String fromAddress;

    @Column(name = "committed")
    private Boolean committed;

    @Column(name = "status")
    private Integer status;

    @Column(name = "failed_reason")
    private String reason;

    @Column(name = "txid")
    private String txid;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "height")
    private Long height;

    @Column(name = "state")
    private Integer state;

    @Column(name = "gas")
    private BigDecimal gas;

    @Transient
    private boolean allowRetry = true;
}
