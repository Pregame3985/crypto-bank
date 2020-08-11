package com.tokimi.common.chain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SenderDTO {

    private String txid;

    private Integer index;

    private Long utxoId;

//    private ScriptSig scriptSig;

    private Long sequence;

    private String address;

    private BigDecimal amount;

    private Integer type;

    private Long tokenId;
}
