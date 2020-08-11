package com.tokimi.common.message.service;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.tokimi.common.message.model.MessagePublisher;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
@Component
public class PubsubMessageService implements MessageService {

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void send(MessagePublisher messagePublisher, Object message) {

        try {
            Publisher publisher = (Publisher) messagePublisher.getPublisher();
            ByteString data = ByteString.copyFromUtf8(objectMapper.writeValueAsString(message));
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            publisher.publish(pubsubMessage).get();
        } catch (Exception e) {
            log.error("error publishing message '{}'", message);
        }
    }
}
