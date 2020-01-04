package com.mcb.address.manager.controller;

import com.mcb.address.manager.model.ErrorDTO;
import com.mcb.address.manager.model.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;

import java.util.concurrent.Callable;

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
            response.setError(new ErrorDTO("NOT_FOUND", "object not found"));
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
            response.setError(new ErrorDTO(e.getClass().getSimpleName(), e.getLocalizedMessage()));
        }
    }

    protected static <RS> boolean validate(BindingResult bindingResult, GenericResponse<RS> response) {

        boolean result = !bindingResult.hasErrors();

        if (!result) {
            response.setSuccess(false);
            response.setError(new ErrorDTO("VALIDATE_ERROR", bindingResult.getAllErrors().toString()));
        }

        return result;
    }
}
