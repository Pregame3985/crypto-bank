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
import lombok.ToString;

/**
 * @author william
 */
@Getter
@Setter
@ToString
@Entity
@Table(name = "chn_fund_histories")
public class FundHistory extends AbstractPersistableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "type")
    private Integer type;

    @Column(name = "type_str")
    private String typeStr;

    @Column(name = "token_id", nullable = false)
    private Long tokenId;

    @Column(name = "token_name")
    private String tokenName;

    @Column(name = "txid", updatable = false)
    private String txid;

    @Column(name = "`index`")
    private Integer index;

    @Column(name = "from_address")
    private String fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    @Column(name = "memo")
    private String memo;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "committed_height")
    private Long committedHeight;

    @Column(name = "confirmed_height")
    private Long confirmedHeight;

    @Column(name = "confirmations")
    private Long confirmations;

    @Column(name = "due_confirmations")
    private Integer dueConfirmations;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "status")
    private Integer status;

    @Column(name = "status_str")
    private String statusStr;

    @Column(name = "failed_code")
    private String failedCode;

    @Column(name = "failed_reason")
    private String failedReason;

    @Column(name = "url_for_tx")
    private String urlForTx;

    @Column(name = "url_for_address")
    private String urlForAddress;

    @Column(name = "hash")
    private String hash;
}