package com.mcb.address.manager.config;

import com.mcb.address.manager.core.EthWalletEngine;
import com.mcb.address.manager.core.WalletEngine;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;

@Configuration
public class EthWalletEngineConfig {

    @Setter
    @Resource
    private Environment environment;

    @Bean
    public WalletEngine ethWalletEngine() {

        int currentNetwork = environment.getRequiredProperty("app.chain.network", int.class);

        return new EthWalletEngine(currentNetwork);
    }
}