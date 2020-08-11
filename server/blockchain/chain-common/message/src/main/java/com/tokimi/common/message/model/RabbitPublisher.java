package com.tokimi.common.message.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class RabbitPublisher implements MessagePublisher {

    private String topicName;

    private String key;

    public RabbitPublisher(String topicName, String key) {
        this.topicName = topicName;
        this.key = key;
    }

    @Override
    public Object getPublisher() {
        return this;
    }
}