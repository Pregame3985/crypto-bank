package com.tokimi.common.pubsub.config;

import java.io.IOException;

import com.google.cloud.ServiceOptions;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author william
 */
@Configuration
public class PublisherConfig {

    @Bean
    public Publisher depositTrackPublisher() throws IOException {
        return Publisher.newBuilder(ProjectTopicName.of(
                ServiceOptions.getDefaultProjectId(), FlowConfigurer.CHAIN_DEPOSIT_TRACK_TOPIC_NAME
        )).build();
    }

    @Bean
    public Publisher withdrawTrackPublisher() throws IOException {
        return Publisher.newBuilder(ProjectTopicName.of(
                ServiceOptions.getDefaultProjectId(), FlowConfigurer.CHAIN_WITHDRAW_TRACK_TOPIC_NAME
        )).build();
    }

    @Bean
    public Publisher chainWithdrawRequestPublisher() throws IOException {
        return Publisher.newBuilder(ProjectTopicName.of(
                ServiceOptions.getDefaultProjectId(), FlowConfigurer.CHAIN_WITHDRAW_REQUEST_TOPIC_NAME
        )).build();
    }
}