package com.tokimi.common.node;

import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

import com.tokimi.common.Utils;

import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
@Getter
@Setter
public class DefaultNodeManager {

    @Getter
    private WebClient signerNode;

    @Getter
    private WebClient networkNode;

    public DefaultNodeManager(NodeInfo networkNodeInfo, NodeInfo signingNodeInfo) {
        this.signerNode = buildNode(signingNodeInfo);
        this.networkNode = buildNode(networkNodeInfo);
    }

    private WebClient buildNode(NodeInfo nodeInfo) {

        WebClient node = null;

        String username = nodeInfo.getUsername();
        String password = nodeInfo.getPassword();
        String url = nodeInfo.getUrl();

        if (!Utils.isEmpty(url)) {
            WebClient.Builder builder = WebClient.builder().baseUrl(url);

            if (!Utils.isEmpty(username) && !Utils.isEmpty(password)) {
                builder = builder.filter(basicAuthentication(username, password));
            }

            node = builder.build();
        }

        return node;
    }
}