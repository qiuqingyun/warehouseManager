package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author QIU
 */
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    List<Owner> findAllByNameContaining(String name);
}
