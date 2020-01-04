package com.mcb.address.manager.dao;

import com.mcb.address.manager.entity.Properties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jingbo.zhang
 */
@Repository
public interface PropertiesDAO extends JpaRepository<Properties, Long> {
}
