package com.tokimi.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
@NoArgsConstructor
public class ErrorDTO {

    private ErrorConstants code;

    private String message;

    public ErrorDTO(ErrorConstants code, String message) {
        this.code = code;
        this.message = message;
    }

}
