package com.tokimi.common.chain.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author william
 */
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class UnsweepAmoutDTO {

    @NonNull
    private Long tokenId;

    @NonNull
    private String amount;
}
