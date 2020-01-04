package com.mcb.address.manager.dao;

import com.mcb.address.manager.entity.PlatformWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface PlatformWalletDAO extends JpaRepository<PlatformWallet, PlatformWallet.Id> {
}