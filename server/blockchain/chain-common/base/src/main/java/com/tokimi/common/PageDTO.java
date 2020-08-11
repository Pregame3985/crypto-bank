package com.tokimi.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * @author william
 */
@Setter
@Getter
@RequiredArgsConstructor
public class PageDTO {

    private int totalPages;

    private long totalElements;

    private int number;

    private int size;

    private int numberOfElements;
}
