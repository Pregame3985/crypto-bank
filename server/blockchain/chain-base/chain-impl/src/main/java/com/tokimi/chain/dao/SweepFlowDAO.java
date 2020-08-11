package com.tokimi.chain.dao;

import com.tokimi.chain.entity.SweepFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface SweepFlowDAO extends JpaRepository<SweepFlow, Long> {

}