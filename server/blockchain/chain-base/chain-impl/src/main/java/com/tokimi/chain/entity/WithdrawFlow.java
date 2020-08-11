package com.tokimi.chain.entity;

import com.tokimi.common.chain.utils.TxType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Getter
@Setter
@Entity(name = "chn_withdraw_flows")
public class WithdrawFlow extends TokenFlow {

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "output_count")
    private Integer outputCount;

    @Column(name = "committed_height")
    private Long committedHeight;

    @Column(name = "confirmed_height")
    private Long confirmedHeight;

    @Column(name = "failed_reason")
    private String failedReason;

    @Column(name = "failed_retries")
    private Integer failedRetries;

    @Column(name = "withdraw_request_id")
    private Long requestId;

    @Transient
    private String memo;

    @Transient
    private TxType type;

    @Transient
    private Long userId;

    @Transient
    private String toAddress;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "signed_raw_tx", columnDefinition = "TEXT", updatable = false)
    private String signedRawTx;

    @Column(name = "status_str")
    private String statusStr;

    @OrderBy("id ASC")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "withdrawFlow")
    private List<WithdrawDetail> details = new ArrayList<>();
}
