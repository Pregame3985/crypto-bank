package com.mcb.address.manager.service;

import com.google.cloud.pubsub.v1.Publisher;

/**
 * @author william
 */
public interface MessageService {

    String send(Publisher publisher, Object message);
}
