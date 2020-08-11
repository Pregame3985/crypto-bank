package com.tokimi.common.node;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */

@Getter
@Setter
public abstract class NodeInfo {

    private String url;

    private String username;

    private String password;
}
