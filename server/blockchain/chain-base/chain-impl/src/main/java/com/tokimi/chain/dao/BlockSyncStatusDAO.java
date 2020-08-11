package com.tokimi.chain.dao;

import com.tokimi.chain.entity.BlockSyncStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface BlockSyncStatusDAO extends JpaRepository<BlockSyncStatus, Long> {

}
