package com.tokimi.chain.service.tx;

/**
 * @author william
 */
public interface Serializer {

    void serialize();

    interface Item {

        String getId();

        String getName();

        String getValue();

        String getDesc();

        boolean isWitness();
    }
}
