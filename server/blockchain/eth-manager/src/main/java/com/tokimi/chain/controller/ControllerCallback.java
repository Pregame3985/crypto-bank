package com.tokimi.chain.controller;

import com.tokimi.common.GenericResponse;

import java.text.ParseException;

/**
 * @author william
 */
@FunctionalInterface
public interface ControllerCallback<RS> {

    void execute(GenericResponse<RS> response) throws ParseException;
}
