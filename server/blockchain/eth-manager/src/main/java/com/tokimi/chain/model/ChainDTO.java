package com.tokimi.chain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

/**
 * @author william
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.chain")
public class ChainDTO {

    @NotBlank
    private Long id;

    private Long gasId;

    private Integer network;

    private Integer retryTimes;
}