package com.tokimi.common.network;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@NoArgsConstructor
public class ChainError {

    private Integer code;

    private String message;
}

