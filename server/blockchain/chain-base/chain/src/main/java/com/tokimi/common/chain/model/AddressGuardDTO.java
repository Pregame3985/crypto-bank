package com.tokimi.common.chain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author william
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class AddressGuardDTO {

    @NonNull
    private Long tokenId;

    private Long gasTokenId;

    @NonNull
    private Long userId;

    @NonNull
    private String address;
}
