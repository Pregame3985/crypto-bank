package com.tokimi.common.chain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tokimi.common.chain.utils.TokenType;
import com.tokimi.common.chain.utils.TxType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author william
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawRequestDTO {

    private Long id;

    private Integer type;

    private Long userId;

    private Long tokenId;

    private TokenType tokenType;

    private TxType txType;

    private String toAddress;

    private String fromAddress;

    private String memo;

    private BigDecimal amount;

    /**
     * for refactor
     */
    private String requestId;

    private boolean committed;

    private Integer status;

    private String errorCode;

    private String reason;

    private String txid;

    private boolean confirmed;

    private Long height;

    private Integer state;

    private BigDecimal mineFee;

    /**
     * for handle refund tx
     **/
    private Integer targetTokenId;

    private Integer targetToAddress;

    private Integer targetMemo;

}
