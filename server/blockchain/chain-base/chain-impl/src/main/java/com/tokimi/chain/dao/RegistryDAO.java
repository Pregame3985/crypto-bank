package com.tokimi.chain.dao;

import com.tokimi.chain.entity.Registry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface RegistryDAO extends JpaRepository<Registry, Long> {
}
