package com.tokimi.common.message.service;

import java.util.UUID;

import javax.annotation.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tokimi.common.message.model.MessagePublisher;
import com.tokimi.common.message.model.RabbitPublisher;

import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author william
 */
@Slf4j
@Component
public class RabbitmqMessageService implements MessageService, RabbitTemplate.ConfirmCallback {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Override
    public void send(MessagePublisher messagePublisher, Object message) {

        try {
            String data = objectMapper.writeValueAsString(message);

            RabbitPublisher rabbitPublisher = (RabbitPublisher) messagePublisher;

            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

            // rabbitTemplate.convertAndSend(rabbitPublisher.getTopicName(),
            // rabbitPublisher.getKey(),
            // new Message(data.getBytes(), messageProperties), correlationData);
            rabbitTemplate.convertAndSend(rabbitPublisher.getTopicName(), rabbitPublisher.getKey(), data.getBytes(),
                    correlationData);
        } catch (Exception e) {
            log.error("error publishing message '{}'", message);
        }
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("send message successful: {}", correlationData.getId());
        } else {
            log.warn("send message failed: {}, reason: {}", correlationData.getId(), cause);
        }
    }
}
