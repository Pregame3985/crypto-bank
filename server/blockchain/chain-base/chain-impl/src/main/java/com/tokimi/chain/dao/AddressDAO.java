package com.tokimi.chain.dao;

import com.tokimi.chain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface AddressDAO extends JpaRepository<Address, Long> {

}
