package com.tokimi.chain.controller;

import com.tokimi.common.ErrorDTO;
import com.tokimi.common.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.util.concurrent.Callable;

import static com.tokimi.common.ErrorConstants.CONTROLLER_ERROR;

/**
 * @author william
 */
@Slf4j
public class ControllerTemplate {

    public static <RS> Callable<GenericResponse<RS>> spawnAction(ControllerCallback<RS> callback) {
        return spawnAction(null, callback);
    }

    public static <RS> Callable<GenericResponse<RS>> spawnAction(BindingResult bindingResult,
                                                                 ControllerCallback<RS> callback) {
        return () -> call(bindingResult, callback);
    }

    public static <RS> GenericResponse<RS> call(ControllerCallback<RS> callback) throws Exception {
        GenericResponse<RS> response = ControllerTemplate.call(null, callback);
        RS data = response.getData();
        if (null == data && null == response.getError()) {
            ErrorDTO errorDTO = new ErrorDTO(CONTROLLER_ERROR, "object not found");
            errorDTO.setMessage("NOT_FOUND");
            response.setError(errorDTO);
        }
        return response;
    }

    public static <RS> GenericResponse<RS> call(BindingResult bindingResult, ControllerCallback<RS> callback) {

        GenericResponse<RS> response = new GenericResponse<>();

        if (null != bindingResult) {

            log.debug("action with validator");

            boolean result = validate(bindingResult, response);

            if (result && null != callback) {
                execute(callback, response);
            }
        } else {
            log.debug("action without validator");

            if (null != callback) {
                execute(callback, response);
            }
        }

        return response;
    }

    private static <RS> void execute(ControllerCallback<RS> callback, GenericResponse<RS> response) {
        try {
            callback.execute(response);
        } catch (Exception e) {
            ErrorDTO errorDTO = new ErrorDTO(CONTROLLER_ERROR, e.getMessage());
            errorDTO.setMessage(e.getClass().getSimpleName());
            response.setError(errorDTO);
        }
    }

    protected static <RS> boolean validate(BindingResult bindingResult, GenericResponse<RS> response) {

        boolean result = !bindingResult.hasErrors();

        if (!result) {
            response.setSuccess(false);
            ErrorDTO errorDTO = new ErrorDTO(CONTROLLER_ERROR, bindingResult.getAllErrors().toString());
            errorDTO.setMessage("VALIDATE_ERROR");
            response.setError(errorDTO);
        }

        return result;
    }
}
