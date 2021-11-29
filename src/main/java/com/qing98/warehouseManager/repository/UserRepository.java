package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author QIU
 */
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User findUserByUsername(String username);
}
