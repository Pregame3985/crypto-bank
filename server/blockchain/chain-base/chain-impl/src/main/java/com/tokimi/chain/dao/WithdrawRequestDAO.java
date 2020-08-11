package com.tokimi.chain.dao;

import com.tokimi.chain.entity.WithdrawRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface WithdrawRequestDAO extends JpaRepository<WithdrawRequest, Long>, JpaSpecificationExecutor<WithdrawRequest> {

}