package com.tokimi.chain.dao;

import com.tokimi.chain.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface PropertyDAO extends JpaRepository<Property, Integer> {
}
