package com.tokimi.common.rabbitmaq.config;

/**
 * @author william
 */
public interface FlowConfigurer {

    String CHAIN_TRACK_TOPIC_NAME = "chain.track";

    String CHAIN_TRACK_DEPOSIT_ROUTING_KEY_NAME = "chain.track.deposit";

    String CHAIN_TRACK_WITHDRAW_ROUTING_KEY_NAME = "chain.track.withdraw";

    String CHAIN_REQUEST_TOPIC_NAME = "chain.request";

    String CHAIN_ADDRESS_TOPIC_NAME = "chain.address";

    String BINARY_OPTION_TOPIC_NAME = "binary.option";
}
