package com.tokimi.chain.controller;

import com.tokimi.common.GenericResponse;
import com.tokimi.common.chain.service.withdraw.WithdrawService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author william
 */
@Slf4j
@RestController
@RequestMapping(value = "/withdraw")
public class WithdrawController extends GenericHandler<Void> {

    @Setter
    @Resource
    private WithdrawService withdrawService;

    @GetMapping("/failRequest/{requestId}")
    public GenericResponse<Boolean> failRequest(@PathVariable("requestId") Long requestId) throws Exception {
        return ControllerTemplate.call(response -> {

            response.setData(withdrawService.failRequest(requestId));
            response.setSuccess(true);
        });
    }
}
