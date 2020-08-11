package com.tokimi.common.message.service;

import com.tokimi.common.message.model.MessagePublisher;

/**
 * @author william
 */
public interface MessageService {

    void send(MessagePublisher messagePublisher, Object message);
}
