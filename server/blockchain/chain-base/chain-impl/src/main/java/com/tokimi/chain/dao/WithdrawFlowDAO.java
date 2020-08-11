package com.tokimi.chain.dao;

import java.util.List;

import com.tokimi.chain.entity.WithdrawFlow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface WithdrawFlowDAO extends JpaRepository<WithdrawFlow, Long> {

    Page<WithdrawFlow> findAllByStatusAndTokenIdIn(Integer status, List<Long> tokenIds, Pageable pageable);
}