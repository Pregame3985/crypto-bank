package com.tokimi.common.message.model;

/**
 * @author william
 */
public interface MessagePublisher {

    Object getPublisher();

    String getTopicName();
}