package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author william
 */
@Getter
@Setter
@ToString
public class KeyImportDTO {

    private Long tokenId;

    private Long userId;

    private String address;

    private Integer index;

    private boolean isPrivate;
}
