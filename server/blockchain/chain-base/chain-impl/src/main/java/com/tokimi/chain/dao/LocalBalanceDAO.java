package com.tokimi.chain.dao;

import java.math.BigDecimal;
import java.util.List;

import com.tokimi.chain.entity.LocalBalance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface LocalBalanceDAO extends JpaRepository<LocalBalance, Long> {

    List<LocalBalance> findAllByTokenIdAndBalanceGreaterThanOrderByBalanceDesc(Long tokenId, BigDecimal balance);

    List<LocalBalance> findAllByTokenIdAndAddressAndBalanceGreaterThanOrderByBalanceDesc(Long tokenId, String address, BigDecimal balance);
}
