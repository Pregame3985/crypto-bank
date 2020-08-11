package com.tokimi.chain.controller;

import com.tokimi.common.chain.model.TrackDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author william
 */
@Slf4j
@RestController
@RequestMapping(value = "/callback")
public class CallbackController extends GenericHandler<Void> {

    @PostMapping("/test")
    public CallbackResponseDTO addresses(@RequestBody TrackDTO trackDTO) throws Exception {

        CallbackResponseDTO response = new CallbackResponseDTO();

        response.setTrackHash(trackDTO.getTrackHash());
        response.setResult("OK");

        return response;
    }

    @Getter
    @Setter
    private static class CallbackResponseDTO {

        private String trackHash;

        private String result;
    }
}
