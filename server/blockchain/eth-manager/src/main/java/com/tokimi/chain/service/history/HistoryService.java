package com.tokimi.chain.service.history;

import com.tokimi.common.chain.model.TrackDTO;

/**
 * @author william
 */
public interface HistoryService {

    void log(TrackDTO trackDTO);
}