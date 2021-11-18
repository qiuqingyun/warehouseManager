package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * @author QIU
 */
public interface ItemRepository extends JpaRepository<Item, UUID> {
    /**
     * 根据状态查询物品
     * @param status 物品状态
     * @param pageable 分页情况
     * @return 查询结果
     */
    List<Item> findAllByStatus(Item.Status status, Pageable pageable);

    /**
     * 根据状态查询物品数量
     * @param status 物品状态
     * @return 物品数量
     */
    long countAllByStatus(Item.Status status);
}
