package com.mcb.address.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author william
 */
@Slf4j
@Component
public class DefaultMessageService implements MessageService {

    @Setter
    @Resource
    private ObjectMapper objectMapper;

    @Async
    @Override
    public String send(Publisher publisher, Object message) {

        try {
            
            final String[] messageIds = new String[1];
            AtomicInteger countDown = new AtomicInteger(3);

            while (StringUtils.isEmpty(messageIds[0]) && (countDown.get() > 0)) {
                CountDownLatch blocker = new CountDownLatch(1);
                ByteString data = ByteString.copyFromUtf8(objectMapper.writeValueAsString(message));
                PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

                ApiFuture<String> future = publisher.publish(pubsubMessage);

                ApiFutures.addCallback(future, new ApiFutureCallback<String>() {

                    @Override
                    public void onFailure(Throwable throwable) {
                        log.error("message sending error : {}, times : {}", throwable, countDown.get());
                        countDown.decrementAndGet();
                        blocker.countDown();
                    }

                    @Override
                    public void onSuccess(String messageId) {
                        messageIds[0] = messageId;
                        log.info("message sent id : {}, times : {}", messageId, countDown.get());
                        countDown.decrementAndGet();
                        blocker.countDown();
                    }
                });

                blocker.await();
            }
            return messageIds[0];
        } catch (Exception e) {
            log.error("error publishing message {}, error message {}", message, e.getMessage());
        }

        return null;
    }
}
