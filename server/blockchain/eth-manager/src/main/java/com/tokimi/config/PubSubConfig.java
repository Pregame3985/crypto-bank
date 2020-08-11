package com.tokimi.config;

//import com.tokimi.common.rabbitmaq.config.FlowConfigurer;

/**
 * @author william
 */
//@Configuration
public class PubSubConfig extends FlowConfigurerAdapter {

//    @Bean
//    public TopicExchange topic() {
//        return new TopicExchange(FlowConfigurer.CHAIN_REQUEST_TOPIC_NAME);
//    }
//
//    @Bean
//    public Queue withdrawQueue() {
//        return new AnonymousQueue();
//    }
//
//    @Bean
//    public Queue addAddressQueue() {
//        return new AnonymousQueue();
//    }
//
//    @Bean
//    public Binding bindingWithdraw(TopicExchange topic, Queue withdrawQueue) {
//        return BindingBuilder.bind(withdrawQueue).to(topic).with("chain.request.withdraw");
//    }
//
//    @Bean
//    public Binding bindingAddAddress(TopicExchange topic, Queue addAddressQueue) {
//        return BindingBuilder.bind(addAddressQueue).to(topic).with("chain.request.add.address");
//    }
//
//    @Bean
//    public MessagePublisher depositTrackPublisher() {
//        return new RabbitPublisher(FlowConfigurer.CHAIN_TRACK_TOPIC_NAME,
//                FlowConfigurer.CHAIN_TRACK_DEPOSIT_ROUTING_KEY_NAME);
//    }
//
//    @Bean
//    public MessagePublisher withdrawTrackPublisher() {
//        return new RabbitPublisher(FlowConfigurer.CHAIN_TRACK_TOPIC_NAME,
//                FlowConfigurer.CHAIN_TRACK_WITHDRAW_ROUTING_KEY_NAME);
//    }
//
//    @Bean
//    public TopicExchange chainTrackTopic() {
//        return new TopicExchange(FlowConfigurer.CHAIN_TRACK_TOPIC_NAME);
//    }
//
//    @Bean
//    public Queue chainTrackQueue() {
//        return new AnonymousQueue();
//    }
//
//    @Bean
//    public Binding bindingChainTrack(TopicExchange chainTrackTopic, Queue chainTrackQueue) {
//        return BindingBuilder.bind(chainTrackQueue).to(chainTrackTopic).with("chain.track.#");
//    }

}