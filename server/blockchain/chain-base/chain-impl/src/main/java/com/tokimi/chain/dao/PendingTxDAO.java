package com.tokimi.chain.dao;

import com.tokimi.chain.entity.PendingTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author william
 */
@Repository
public interface PendingTxDAO extends JpaRepository<PendingTx, Long>, JpaSpecificationExecutor<PendingTx> {

    List<PendingTx> findTop20ByProcessedAndTokenTypeOrderById(Boolean processed, Integer tokenType);
}
