package com.mcb.address.manager.config;

import com.mcb.address.manager.core.BtcWalletEngine;
import com.mcb.address.manager.core.WalletEngine;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class BtcWalletEngineConfig {

    @Setter
    @Resource
    private Environment environment;

    @Bean
    public WalletEngine btcWalletEngine() {

        int currentNetwork = environment.getRequiredProperty("app.chain.network", int.class);

        return new BtcWalletEngine(currentNetwork);
    }
}