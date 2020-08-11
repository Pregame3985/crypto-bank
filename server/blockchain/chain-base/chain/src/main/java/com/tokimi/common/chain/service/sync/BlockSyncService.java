package com.tokimi.common.chain.service.sync;

import com.tokimi.common.chain.model.BlockDTO;
import reactor.core.publisher.Mono;

/**
 * @author william
 */
public interface BlockSyncService {

    void parseBlock(BlockDTO localBlockDTO);

    void resyncTokenTx(String txid);

    void resyncNativeTx(String txid);

    Mono<BlockDTO> sync(BlockDTO localBlockDTO, Long height);
}
