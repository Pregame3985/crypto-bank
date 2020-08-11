package com.tokimi.chain.controller;

import com.tokimi.common.GenericResponse;
import com.tokimi.common.chain.service.wallet.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @author william
 */
@Slf4j
@RestController
@RequestMapping(value = "/wallet")
public class WalletController extends GenericHandler<Boolean> {

    @Resource
    private WalletService defaultWalletService;

    @GetMapping("/balance/sync/{tokenId}/{address}")
    public GenericResponse<BigDecimal> sync(@PathVariable("tokenId") Long tokenId,
                                            @PathVariable("address") String address) throws Exception {
        return ControllerTemplate.call(response -> {

            response.setData(defaultWalletService.sync(tokenId, null, address));
            response.setSuccess(true);
        });
    }

    @GetMapping("/balance/syncAll/{tokenId}")
    public GenericResponse<Boolean> sync(@PathVariable("tokenId") Long tokenId) throws Exception {
        return ControllerTemplate.call(response -> {

            response.setData(true);

            if (defaultWalletService.isSupport(tokenId)) {
                defaultWalletService.syncAll(tokenId);
            }
            response.setSuccess(true);
        });
    }

    @GetMapping("/balance/{assetId}/{address}")
    public GenericResponse<BigDecimal> balance(@PathVariable("assetId") Long assetId,
                                               @PathVariable("address") String address) throws Exception {
        return ControllerTemplate.call(response -> {

            if (defaultWalletService.isSupport(assetId)) {
                response.setData(defaultWalletService.balance(address, assetId));
            }
            response.setSuccess(true);
        });
    }

    @PostMapping("/withdrawRequest")
    public GenericResponse<Boolean> withdrawRequest(@PathVariable("assetId") Long assetId,
                                         @PathVariable("address") String address) throws Exception {
        return ControllerTemplate.call(response -> {

//            if (defaultWalletService.isSupport(assetId)) {
//                response.setData(defaultWalletService.balance(address, assetId));
//            }
            response.setSuccess(true);
        });
    }
}