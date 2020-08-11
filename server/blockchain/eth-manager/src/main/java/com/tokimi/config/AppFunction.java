package com.tokimi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author william
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.function")
public class AppFunction {

    private boolean sync;

    private boolean deposit;

    private boolean withdraw;

    private boolean sweep;
}
