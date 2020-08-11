package com.tokimi.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class GenericResponse<T> {

    private boolean success;

    private ErrorDTO error;

    private T data;

    private PageDTO page;
}
