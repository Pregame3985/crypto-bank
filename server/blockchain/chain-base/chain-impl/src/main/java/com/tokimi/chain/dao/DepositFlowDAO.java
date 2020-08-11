package com.tokimi.chain.dao;

import com.tokimi.chain.entity.DepositFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author william
 */
@Repository
public interface DepositFlowDAO extends JpaRepository<DepositFlow, Long>, JpaSpecificationExecutor<DepositFlow> {

    List<DepositFlow> findTop20ByFromAddress(String fromAddress);

}