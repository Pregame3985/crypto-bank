package com.mcb.address.manager.dao;

import com.mcb.address.manager.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface UserAddressDAO extends JpaRepository<UserAddress, Long> {
}
