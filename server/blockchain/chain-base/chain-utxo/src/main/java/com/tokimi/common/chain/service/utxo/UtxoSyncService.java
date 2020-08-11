package com.tokimi.common.chain.service.utxo;

import com.tokimi.common.chain.service.sync.BlockSyncService;

/**
 * @author william
 */
public interface UtxoSyncService extends BlockSyncService {

    void resyncUtxo(String txid);

    void resyncReceiverUtxoFrom(Long height);

    void resyncSenderUtxoFrom(Long height);
}
