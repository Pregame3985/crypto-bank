package com.tokimi.chain.dao;

import com.tokimi.chain.entity.ExchangeWallet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface ExchangeWalletDAO extends JpaRepository<ExchangeWallet, Long> {
}