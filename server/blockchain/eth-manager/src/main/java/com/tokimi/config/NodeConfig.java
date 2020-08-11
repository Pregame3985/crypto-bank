package com.tokimi.config;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokimi.address.manager.core.EthWalletEngine;
import com.tokimi.address.manager.core.WalletEngine;
import com.tokimi.chain.model.ChainDTO;
import com.tokimi.chain.node.NetworkNodeInfo;
import com.tokimi.chain.node.SigningNodeInfo;
import com.tokimi.common.network.rpc.DefaultRpcChainAgent;
import com.tokimi.common.network.rpc.JsonRpcAgent;
import com.tokimi.common.node.DefaultNodeManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
@Configuration
public class NodeConfig {

    @Setter
    @Resource
    private ChainDTO chainDTO;

    @Setter
    @Resource
    private NetworkNodeInfo networkNodeInfo;

    @Setter
    @Resource
    private SigningNodeInfo signingNodeInfo;

    @Setter
    @Resource
    private ObjectMapper objectMapper;

    @Bean
    public JsonRpcAgent jsonRpcAgent() {

        DefaultNodeManager defaultNodeManager = new DefaultNodeManager(networkNodeInfo, signingNodeInfo);

        return new DefaultRpcChainAgent(defaultNodeManager.getNetworkNode(), defaultNodeManager.getSignerNode(), objectMapper, chainDTO.getRetryTimes());
    }

    @Bean
    public WalletEngine walletEngine() {
        return new EthWalletEngine(chainDTO.getNetwork());
    }
}
