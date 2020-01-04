package com.mcb.address.manager.controller;

import com.mcb.address.manager.model.AddressDTO;
import com.mcb.address.manager.model.GenericResponse;
import com.mcb.address.manager.service.AddressService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author william
 */
@Slf4j
@RestController
@RequestMapping(value = "/address")
public class AddressController {

    static ThreadLocal<Long> startTime = new ThreadLocal();

    @Setter
    @Resource
    private AddressService addressService;

    @GetMapping("/{userId}/{tokenId}")
    public GenericResponse<AddressDTO> retrieve(@PathVariable("userId") Integer userId,
                                                @PathVariable("tokenId") Integer tokenId) throws Exception {
        startTime.set(System.currentTimeMillis());
        return ControllerTemplate.call(response -> {

            AddressDTO addressDTO = new AddressDTO();

            addressDTO.setUserId(userId);
            addressDTO.setTokenId(tokenId);

            log.info("request params: {}", addressDTO);

            response.setData(addressService.getAddress(addressDTO));
            response.setSuccess(true);
            log.info("request consume {} ms", (System.currentTimeMillis() - startTime.get()));
        });
    }

    @PostMapping("/isValid")
    public GenericResponse<Boolean> isValid(@RequestBody AddressDTO request) throws Exception {
        return ControllerTemplate.call(response -> {

            response.setData(addressService.isValid(request.getTokenId(), request.getAddress()));
            response.setSuccess(true);
        });
    }

    @PostMapping(value = "/generate")
    public GenericResponse<AddressDTO> generate(@RequestBody AddressDTO request) throws Exception {

        return ControllerTemplate.call(response -> {

            log.info("request params: {}", request);

            response.setData(addressService.generateAddress(request));
            response.setSuccess(true);
        });
    }

    @GetMapping(value = "/fix/{tokenId}")
    public GenericResponse<Boolean> fix(@PathVariable("tokenId") Integer tokenId) throws Exception {

        return ControllerTemplate.call(response -> {

            response.setData(addressService.fix(tokenId));
            response.setSuccess(true);
        });
    }
}
