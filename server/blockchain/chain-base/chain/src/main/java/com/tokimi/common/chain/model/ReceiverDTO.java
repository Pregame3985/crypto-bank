package com.tokimi.common.chain.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReceiverDTO {

    private BigDecimal amount;

    @JsonProperty("vout")
    private Integer index;

    private String scriptPubKey;

    private String address;

    private String propertyId;

    private Long requestId;

    private String memo;

    private Integer type;

    private Long userId;

    private Long tokenId;

    private boolean tokimiAddress;
}
