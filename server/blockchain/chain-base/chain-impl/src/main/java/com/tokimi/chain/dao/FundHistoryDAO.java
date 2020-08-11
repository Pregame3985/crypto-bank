package com.tokimi.chain.dao;

import com.tokimi.chain.entity.FundHistory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface FundHistoryDAO extends JpaRepository<FundHistory, Long> {

}