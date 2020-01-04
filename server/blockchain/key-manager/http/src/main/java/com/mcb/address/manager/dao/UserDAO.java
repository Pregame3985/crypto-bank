package com.mcb.address.manager.dao;

import com.mcb.address.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author william
 */
@Repository
public interface UserDAO extends JpaRepository<User, Integer> {
}
