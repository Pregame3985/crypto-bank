package com.tokimi.chain.dao;

import com.tokimi.chain.entity.BlockInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface BlockInfoDAO extends JpaRepository<BlockInfo, Long> {

    BlockInfo findTopByReorgOrderByIdDesc(Boolean reorg);

}
