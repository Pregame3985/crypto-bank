package com.tokimi.common.chain.service.sequence;

/**
 * @author william
 */
public interface SequenceService  {

    Long sync(String address);
    
    Long get(String address);

    void update(String address, Long seq);
}
