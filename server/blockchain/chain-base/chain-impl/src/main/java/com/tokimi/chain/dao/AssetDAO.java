package com.tokimi.chain.dao;

import com.tokimi.chain.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface AssetDAO extends JpaRepository<Asset, Long> {

}