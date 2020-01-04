package com.mcb.address.manager.controller;

import com.mcb.address.manager.model.GenericResponse;

import java.text.ParseException;

/**
 * @author william
 */
@FunctionalInterface
public interface ControllerCallback<RS> {

    void execute(GenericResponse<RS> response) throws ParseException;
}
