package com.mcb.address.manager.model;

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

    private Integer tokenId;

    private Long userId;

    private String address;

    private Integer index;

    private boolean isPrivate;
}
