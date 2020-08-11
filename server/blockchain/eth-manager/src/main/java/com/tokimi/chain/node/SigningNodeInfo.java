package com.tokimi.chain.node;

import com.tokimi.common.node.NodeInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author william
 */
@Component
@ConfigurationProperties(prefix = "app.chain.node.signing")
public class SigningNodeInfo extends NodeInfo {

}
