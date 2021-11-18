package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.Item;
import com.qing98.warehouseManager.entity.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author QIU
 */
public interface OwnerRepository extends JpaRepository<Owner, Long> {
}
