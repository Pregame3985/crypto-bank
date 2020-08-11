package com.tokimi.common.chain.service.sync;

import com.tokimi.common.chain.model.BlockDTO;

import reactor.core.publisher.Mono;

/**
 * @author william
 */
public interface BlockService {

    BlockDTO getBestBlock();

    BlockDTO getBlock(Long height);

    BlockDTO getBlock(String hash);

    void saveBlock(BlockDTO blockDTO);

    BlockDTO getLocalBestBlock();

    BlockDTO getLocalBlock(String hash);

    BlockDTO getLocalBlock(Long height);

    Mono<BlockDTO> getBestBlockReactive();

    Mono<BlockDTO> getBlockReactive(Long height);

    Mono<BlockDTO> getBlockReactive(String hash);

    void updateBlockStatus(Long remoteHeight, Long localHeight);
}
