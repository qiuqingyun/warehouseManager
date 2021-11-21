package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * @author QIU
 */
public interface ItemRepository extends JpaRepository<Item, UUID> {
    /**
     * 根据状态查询物品
     *
     * @param status   物品状态
     * @param pageable 分页情况
     * @return 查询结果
     */
    List<Item> findAllByStatus(Item.Status status, Pageable pageable);

    /**
     * 根据状态查询物品数量
     *
     * @param status 物品状态
     * @return 物品数量
     */
    long countAllByStatus(Item.Status status);

    List<Item> findAllByNameContaining(String name, Pageable pageable);

    long countAllByNameContaining(String name);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    long countAllByOwnerId(Long ownerId);


    List<Item> findAllByDateRecord(LocalDateTime dateRecord, Pageable pageable);

    long countAllByDateRecord(LocalDateTime dateRecord);

    List<Item> findAllByDateRecordBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    long countAllByDateRecordBetween(LocalDateTime dateStart, LocalDateTime dateEnd);


    List<Item> findAllByDateInto(LocalDateTime dateInto, Pageable pageable);

    long countAllByDateInto(LocalDateTime dateInto);

    List<Item> findAllByDateIntoBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    long countAllByDateIntoBetween(LocalDateTime dateStart, LocalDateTime dateEnd);


    List<Item> findAllByDateLeave(LocalDateTime dateLeave, Pageable pageable);

    long countAllByDateLeave(LocalDateTime dateLeave);

    List<Item> findAllByDateLeaveBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    long countAllByDateLeaveBetween(LocalDateTime dateStart, LocalDateTime dateEnd);


    List<Item> findAllByDescriptionContaining(String description, Pageable pageable);

    long countAllByDescriptionContaining(String description);


    List<Item> findAllByLength(Float length, Pageable pageable);

    long countAllByLength(Float length);

    List<Item> findAllByLengthBetween(Float rangeStart, Float rangeEnd, Pageable pageable);

    long countAllByLengthBetween(Float rangeStart, Float rangeEnd);


    List<Item> findAllByWidth(Float width, Pageable pageable);

    long countAllByWidth(Float width);

    List<Item> findAllByWidthBetween(Float rangeStart, Float rangeEnd, Pageable pageable);

    long countAllByWidthBetween(Float rangeStart, Float rangeEnd);


    List<Item> findAllByHeight(Float height, Pageable pageable);

    long countAllByHeight(Float height);

    List<Item> findAllByHeightBetween(Float rangeStart, Float rangeEnd, Pageable pageable);

    long countAllByHeightBetween(Float rangeStart, Float rangeEnd);


    List<Item> findAllByArchitectureContaining(String architecture, Pageable pageable);

    long countAllByArchitectureContaining(String architecture);

}
