package com.tokimi.chain.controller;

import com.tokimi.common.GenericResponse;
import com.tokimi.common.PageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * @author william
 */
public abstract class GenericHandler<T> {

    protected void extractResponse(GenericResponse<List<T>> response, Page<T> data) {

        response.setData(data.getContent());

        PageDTO pageDTO = new PageDTO();

        pageDTO.setNumber(data.getNumber() + 1);
        pageDTO.setNumberOfElements(data.getNumberOfElements());
        pageDTO.setSize(data.getSize());
        pageDTO.setTotalPages(data.getTotalPages());
        pageDTO.setTotalElements(data.getTotalElements());

        response.setPage(pageDTO);
        response.setSuccess(true);
    }

    int defaultPageSize() {
        return 10;
    }

    int defaultPageNumber() {
        return 0;
    }

    protected Pageable getPageRequest(Integer pageNumber, Integer pageSize) {

        return getPageRequest(pageNumber, pageSize, null);
    }

    protected Pageable getPageRequest(Integer pageNumber, Integer pageSize, Sort sort) {

        PageRequest pageRequest;

        if (null == sort) {
            sort = getSort();
        }

        if (null != sort) {
            pageRequest = PageRequest.of(setAndGetPageNumber(pageNumber), setAndGetPageSize(pageSize), sort);
        } else {
            pageRequest = PageRequest.of(setAndGetPageNumber(pageNumber), setAndGetPageSize(pageSize));
        }
        return pageRequest;
    }


    protected Sort getSort() {
        return null;
    }

    protected int setAndGetPageSize(Integer pageSize) {
        if (null == pageSize) {
            return defaultPageSize();
        } else {
            return pageSize;
        }
    }

    protected int setAndGetPageNumber(Integer pageNumber) {
        if (null == pageNumber) {
            return defaultPageNumber();
        } else {
            if (pageNumber > 0) {
                return pageNumber - 1;
            }
            return pageNumber;
        }
    }
}
