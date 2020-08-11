package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Getter
@Setter
public class AssetDTO {

    private Long id;

    private Integer status;

    private String name;

    private String shortName;

    private String symbol;

    private Long parentId;

    private Integer assetType;

    private Integer type;

    private String addressRegex;

    private BigDecimal minWithdrawCount;

    private Integer depositConfirmations;

    private Integer withdrawConfirmations;

    private List<String> addresses;

    private Integer onNetwork;

    private String txBaseURL;

    private String addressBaseURL;

    private List<PropertyDTO> properties = new ArrayList<>();
}