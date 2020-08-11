package com.tokimi.chain.dao;

import com.tokimi.chain.entity.Utxo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author william
 */
@Repository
public interface UtxoDAO extends JpaRepository<Utxo, Long> {

    List<Utxo> findTop10ByTokenIdAndLockingAndSpendableAndSyncedOrderByAmountDesc(Long tokenId, boolean locking, boolean spendable, boolean synced);

    List<Utxo> findAllByTokenIdAndSyncedAndSpentTxidIsNull(Long tokenId, boolean synced);

    List<Utxo> findAllByTokenIdAndSynced(Long tokenId, boolean synced);
}
