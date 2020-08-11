package com.tokimi.common.chain.service.sync;

import java.util.List;

import javax.transaction.Transactional;

import com.tokimi.chain.service.sync.BlockSyncServiceAdapter;
import com.tokimi.common.chain.model.BlockDTO;
import com.tokimi.common.chain.model.TransactionDTO;
import com.tokimi.common.chain.service.tx.TransactionService;
import com.tokimi.common.chain.service.utxo.UtxoSyncService;

/**
 * @author william
 */
public abstract class UtxoBlockSyncServiceAdapter extends BlockSyncServiceAdapter implements UtxoSyncService {

    protected List<TransactionDTO> getTxSinceBlock(Long height) {
        return resyncUtxoSinceBlock(blockService.getBlock(height));
    }

    private List<TransactionDTO> resyncUtxoSinceBlock(BlockDTO latestBlockDTO) {

        if (null == latestBlockDTO) {
            return null;
        }

        String hash = latestBlockDTO.getHash();
        return listSinceBlock(hash);
    }

    @Override
    @Transactional
    public void resyncUtxo(String txid) {

        TransactionDTO chainTx = getUtxoTransactionService().get(txid, address -> getAddresses().contains(address));

        BlockDTO blockDTO = blockService.getBlock(chainTx.getBlockhash());

        saveUtxo(blockDTO, chainTx);
    }

    @Override
    protected void syncTx(BlockDTO blockDTO, TransactionDTO sinceBlock) {

        super.syncTx(blockDTO, sinceBlock);

        saveUtxo(blockDTO, sinceBlock);
    }

    protected abstract void saveUtxo(BlockDTO blockDTO, TransactionDTO chainTx);

    protected abstract List<TransactionDTO> listSinceBlock(String blockHash);

    protected abstract TransactionService getUtxoTransactionService();

}