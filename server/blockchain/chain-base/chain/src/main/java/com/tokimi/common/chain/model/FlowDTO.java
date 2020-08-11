package com.tokimi.common.chain.model;

import java.math.BigDecimal;
import java.util.List;

import com.tokimi.common.chain.utils.DepositStatus;
import com.tokimi.common.chain.utils.TrackStatus;
import com.tokimi.common.chain.utils.TxType;
import com.tokimi.common.chain.utils.WithdrawStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

/**
 * @author william
 */
@Setter
@Getter
@ToString
public class FlowDTO {

    private Long id;

    // for tx
    private String toAddress;
    private String txid;
    private Integer index;
    private BigDecimal amount;

    // for account
    private Long requestId;
    private Long tokenId;
    private Long userId;
    private String memo;

    // for status
    private TxType txType;
    private DepositStatus depositStatus;
    private WithdrawStatus withdrawStatus;

    // for both deposit and withdraw
    private Long committedHeight;
    private Long confirmedHeight;

    // for track
    private TrackStatus trackStatus;

    @Setter
    @Singular
    private List<String> fromAddresses;

    // for error
    private String errorCode;
    private String errorMsg;
}
