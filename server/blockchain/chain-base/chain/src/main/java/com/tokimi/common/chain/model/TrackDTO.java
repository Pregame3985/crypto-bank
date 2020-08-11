package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author william
 */
@Getter
@Setter
@ToString
public class TrackDTO {

    private Long id;

    private String txid;

    private Integer index;

    private Integer txType;

    private String txTypeStr;

    private Integer depositStatus;

    private Integer withdrawStatus;

    private Integer status;

    private String statusStr;

    private String errorCode;

    private String errorMsg;

    private Long userId;

    private Long opsUserId;

    private Long tokenId;

    private List<String> fromAddress;

    private String toAddress;

    private BigDecimal amount;

    private String memo;

    private Long confirmations;

    private Long committedHeight;

    private Long confirmedHeight;

    private Long currentHeight;

    private String trackHash;

    private String remark;
}