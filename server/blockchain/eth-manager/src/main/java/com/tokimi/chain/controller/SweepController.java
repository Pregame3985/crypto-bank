package com.tokimi.chain.controller;

import com.tokimi.common.GenericResponse;
import com.tokimi.common.chain.model.AddressBalanceDTO;
import com.tokimi.common.chain.model.UnsweepAmoutDTO;
import com.tokimi.common.chain.service.AssetService;
import com.tokimi.common.chain.service.ChainService;
import com.tokimi.common.chain.service.sweep.SweepService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author william
 */
@Slf4j
@RestController
@RequestMapping(value = "/sweep")
public class SweepController extends GenericHandler<Void> {

    @Setter
    @Resource
    private AssetService assetService;

//    @Setter
//    @Resource
//    private List<SweepService> sweepServices;

    @Resource
    private SweepService sweepService;

    @Setter
    @Resource
    private ChainService chainService;

    @GetMapping("/unsweepAmount")
    public GenericResponse<List<UnsweepAmoutDTO>> unsweepAmount() throws Exception {
        return ControllerTemplate.call(response -> {

            List<UnsweepAmoutDTO> data = new ArrayList<>();
//            sweepServices.forEach(sweepService -> data.addAll(sweepService.unsweepAmount()));
            response.setData(data);
            response.setSuccess(true);
        });
    }

    @GetMapping("/listAvailable")
    public GenericResponse<List<AddressBalanceDTO>> listAvailable() throws Exception {
        return ControllerTemplate.call(response -> {

            List<AddressBalanceDTO> result = new ArrayList<>();

            List<Long> myTokenIds = assetService.getMyAssetIds();

//            myTokenIds.forEach(tokenId ->
//                    sweepServices.stream()
//                            .filter(sweepService -> sweepService.isSupport(tokenId))
//                            .forEach(sweepService -> {
//                                List<AddressBalanceDTO> data = sweepService.listAvailableByTokenId(tokenId.longValue());
//                                if (!CollectionUtils.isEmpty(data)) {
//                                    result.addAll(data);
//                                }
//                            }));

            response.setData(result);
            response.setSuccess(true);
        });
    }

    @GetMapping("/listAvailableByTokenId/{tokenId}")
    public GenericResponse<List<AddressBalanceDTO>> listAvailableByTokenId(@PathVariable("tokenId") Long tokenId) throws
            Exception {
        return ControllerTemplate.call(response -> {

            List<AddressBalanceDTO> result = new ArrayList<>();

//            sweepServices.stream()
//                    .filter(sweepService -> sweepService.isSupport(tokenId))
//                    .forEach(sweepService -> {
//                        List<AddressBalanceDTO> data = sweepService.listAvailableByTokenId(tokenId);
//                        if (!CollectionUtils.isEmpty(data)) {
//                            result.addAll(data);
//                        }
//                    });

            response.setData(result);
            response.setSuccess(true);
        });
    }

    @GetMapping("/listAvailableByUserId/{userId}")
    public GenericResponse<List<AddressBalanceDTO>> listAvailableByUserId(@PathVariable("userId") Long userId) throws
            Exception {
        return ControllerTemplate.call(response -> {

            List<AddressBalanceDTO> data = new ArrayList<>();
//            sweepServices.forEach(sweepService -> data.addAll(sweepService.listAvailableByUserId(userId)));
            response.setData(data);
            response.setSuccess(true);
        });
    }

    @GetMapping("/listAvailableByAddress/{address}")
    public GenericResponse<List<AddressBalanceDTO>> listAvailableByAddress(@PathVariable("address") String address) throws
            Exception {
        return ControllerTemplate.call(response -> {

            List<AddressBalanceDTO> data = new ArrayList<>();
//            sweepServices.forEach(sweepService -> data.addAll(sweepService.listAvailableByAddress(address)));
            response.setData(data);
            response.setSuccess(true);
        });
    }

    @GetMapping
    public GenericResponse<Boolean> sweep() throws Exception {
        return ControllerTemplate.call(response -> {

//            sweepServices.forEach(SweepService::sweepAll);
            response.setData(Boolean.TRUE);
            response.setSuccess(true);
        });
    }

    @GetMapping("/user/{userId}")
    public GenericResponse<Boolean> sweepByUserId(@PathVariable("userId") Long userId) throws Exception {
        return ControllerTemplate.call(response -> {

//            sweepServices.forEach(sweepService -> sweepService.sweepByUserId(userId));
            response.setData(Boolean.TRUE);
            response.setSuccess(true);
        });
    }

    @GetMapping("/address/{address}")
    public GenericResponse<Boolean> sweepByAddress(@PathVariable("address") String address) throws Exception {
        return ControllerTemplate.call(response -> {

            List<Long> myTokenIds = assetService.getMyAssetIds();

//            myTokenIds.forEach(tokenId ->
//                    sweepServices.stream()
//                            .filter(sweepService -> sweepService.isSupport(tokenId))
//                            .forEach(sweepService -> sweepService.sweepByAddress(address)));
            sweepService.sweepByAddress(address);

            response.setData(Boolean.TRUE);
            response.setSuccess(true);
        });
    }

    @GetMapping("/failSweep/{sweepId}")
    public GenericResponse<Boolean> failSweep(@PathVariable("sweepId") Long sweepId) throws Exception {
        return ControllerTemplate.call(response -> {

//            sweepServices.forEach(sweepService -> sweepService.fail(chainService.getAssetId(), sweepId));
            response.setData(Boolean.TRUE);
            response.setSuccess(true);
        });
    }
}
