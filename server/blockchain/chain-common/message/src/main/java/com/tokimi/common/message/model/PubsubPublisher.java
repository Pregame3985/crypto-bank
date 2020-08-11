package com.tokimi.common.message.model;

import com.google.cloud.pubsub.v1.Publisher;

import lombok.Getter;
import lombok.Setter;

/**
 * @author william
 */
@Getter
@Setter
public class PubsubPublisher implements MessagePublisher {

    private Publisher publisher;

    private String key;

    public PubsubPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public String getTopicName() {
        return publisher.getTopicNameString();
    }

    @Override
    public Object getPublisher() {
        return publisher;
    }
}