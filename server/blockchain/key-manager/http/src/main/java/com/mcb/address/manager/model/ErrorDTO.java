package com.mcb.address.manager.model;

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

    private String code;

    private String message;

    public ErrorDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
