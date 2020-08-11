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
import java.util.List;

@Getter
@Setter
@Entity(name = "chn_tracks")
public class Track extends AbstractPersistableEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String txid;

    private Integer index;

    @Column(name = "tx_type")
    private Integer txType;

    @Column(name = "deposit_status")
    private Integer depositStatus;

    @Column(name = "withdraw_status")
    private Integer withdrawStatus;

    private Integer status;

    @Column(name = "error_code")
    private String errorCode;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "op_user_id")
    private Long opsUserId;

    @Column(name = "asset_id")
    private Long tokenId;

    @Transient
    private List<String> fromAddress;

    @Column(name = "to_address")
    private String toAddress;

    private BigDecimal amount;

    private String memo;

    private Long confirmations;

    @Column(name = "committed_height")
    private Long committedHeight;

    @Column(name = "confirmed_height")
    private Long confirmedHeight;

    @Column(name = "track_hash", nullable = false)
    private String trackHash;

    private String remark;

    private String result;
}