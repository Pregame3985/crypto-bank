package com.mcb.address.manager.core;

/**
 * @author william
 */
public abstract class SeedRule {

    private static final String DEFAULT_RULE_NAME = "default_with_hd_version_1";

    public String getRuleName() {
        return DEFAULT_RULE_NAME;
    }

    public abstract String performRule();

    public abstract String getSalt();
    
    public abstract Integer getUserId();
}
