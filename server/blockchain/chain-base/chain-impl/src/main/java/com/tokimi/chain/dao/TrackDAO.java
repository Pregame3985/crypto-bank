package com.tokimi.chain.dao;

import com.tokimi.chain.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface TrackDAO extends JpaRepository<Track, Long> {

}