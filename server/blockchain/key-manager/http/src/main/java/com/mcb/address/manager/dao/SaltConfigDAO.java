package com.mcb.address.manager.dao;

import com.mcb.address.manager.entity.SaltConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @description:
 * @auther: conanliu
 * @date: 18-9-19 15:17
 */
@Repository
public interface SaltConfigDAO extends JpaRepository<SaltConfig, Integer> {

}
