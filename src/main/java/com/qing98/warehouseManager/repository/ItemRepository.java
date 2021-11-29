package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.Item;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    /**
     * 根据物品名称模糊查询物品
     *
     * @param name     物品名称
     * @param pageable 分页
     * @return 物品列表
     */
    List<Item> findAllByNameContaining(String name, Pageable pageable);

    /**
     * 根据物品名称模糊查询物品数量
     *
     * @param name 物品名称
     * @return 物品数量
     */
    long countAllByNameContaining(String name);

    /**
     * 查询指定所有者的所有物品，不分页
     *
     * @param ownerId 所有者编号
     * @return 物品列表
     */
    List<Item> findAllByOwnerId(Long ownerId);

    /**
     * 查询指定所有者的所有物品
     *
     * @param ownerId  所有者编号
     * @param pageable 分页
     * @return 物品列表
     */
    List<Item> findAllByOwnerId(Long ownerId, Pageable pageable);

    /**
     * 查询指定所有者的所有物品的数量
     *
     * @param ownerId 所有者编号
     * @return 物品数量
     */
    long countAllByOwnerId(Long ownerId);


    /**
     * 根据登记时间查询物品
     *
     * @param dateRecord 登记时间
     * @param pageable   分页
     * @return 物品列表
     */
    List<Item> findAllByDateRecord(LocalDateTime dateRecord, Pageable pageable);

    /**
     * 根据登记时间查询物品数量
     *
     * @param dateRecord 登记时间
     * @return 物品列表
     */
    long countAllByDateRecord(LocalDateTime dateRecord);

    /**
     * 根据登记时间范围查询物品
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @param pageable  分页
     * @return 物品列表
     */
    List<Item> findAllByDateRecordBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    /**
     * 根据登记时间范围查询物品数量
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @return 物品数量
     */
    long countAllByDateRecordBetween(LocalDateTime dateStart, LocalDateTime dateEnd);


    /**
     * 根据入库时间查询物品
     *
     * @param dateInto 入库时间
     * @param pageable 分页
     * @return 物品列表
     */
    List<Item> findAllByDateInto(LocalDateTime dateInto, Pageable pageable);

    /**
     * 根据入库时间查询物品数量
     *
     * @param dateInto 入库时间
     * @return 物品列表
     */
    long countAllByDateInto(LocalDateTime dateInto);

    /**
     * 根据入库时间范围查询物品
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @param pageable  分页
     * @return 物品列表
     */
    List<Item> findAllByDateIntoBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    /**
     * 根据入库时间范围查询物品数量
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @return 物品数量
     */
    long countAllByDateIntoBetween(LocalDateTime dateStart, LocalDateTime dateEnd);

    /**
     * 根据出库时间查询物品
     *
     * @param dateLeave 出库时间
     * @param pageable  分页
     * @return 物品列表
     */
    List<Item> findAllByDateLeave(LocalDateTime dateLeave, Pageable pageable);

    /**
     * 根据出库时间查询物品数量
     *
     * @param dateLeave 出库时间
     * @return 物品列表
     */
    long countAllByDateLeave(LocalDateTime dateLeave);

    /**
     * 根据出库时间范围查询物品
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @param pageable  分页
     * @return 物品列表
     */
    List<Item> findAllByDateLeaveBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    /**
     * 根据出库时间范围查询物品数量
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @return 物品数量
     */
    long countAllByDateLeaveBetween(LocalDateTime dateStart, LocalDateTime dateEnd);

    /**
     * 根据物品描述模糊查询物品
     *
     * @param description 物品描述
     * @param pageable    分页
     * @return 物品列表
     */
    List<Item> findAllByDescriptionContaining(String description, Pageable pageable);

    /**
     * 根据物品描述模糊查询物品数量
     *
     * @param description 物品描述
     * @return 物品数量
     */
    long countAllByDescriptionContaining(String description);

    /**
     * 根据物品长度查询物品
     *
     * @param length   物品长度
     * @param pageable 分页
     * @return 物品列表
     */
    List<Item> findAllByLength(Float length, Pageable pageable);

    /**
     * 根据物品长度查询物品数量
     *
     * @param length 物品长度
     * @return 物品数量
     */
    long countAllByLength(Float length);

    /**
     * 根据物品长度范围查询物品
     *
     * @param rangeStart 范围开始
     * @param rangeEnd   范围结束
     * @param pageable   分页
     * @return 物品列表
     */
    List<Item> findAllByLengthBetween(Float rangeStart, Float rangeEnd, Pageable pageable);

    /**
     * 根据物品长度范围查询物品数量
     *
     * @param rangeStart 范围开始
     * @param rangeEnd   范围结束
     * @return 物品数量
     */
    long countAllByLengthBetween(Float rangeStart, Float rangeEnd);

    /**
     * 根据物品宽度查询物品
     *
     * @param width    物品宽度
     * @param pageable 分页
     * @return 物品列表
     */
    List<Item> findAllByWidth(Float width, Pageable pageable);

    /**
     * 根据物品宽度查询物品数量
     *
     * @param width 物品宽度
     * @return 物品数量
     */
    long countAllByWidth(Float width);

    /**
     * 根据物品宽度范围查询物品
     *
     * @param rangeStart 范围开始
     * @param rangeEnd   范围结束
     * @param pageable   分页
     * @return 物品列表
     */
    List<Item> findAllByWidthBetween(Float rangeStart, Float rangeEnd, Pageable pageable);

    /**
     * 根据物品宽度范围查询物品数量
     *
     * @param rangeStart 范围开始
     * @param rangeEnd   范围结束
     * @return 物品数量
     */
    long countAllByWidthBetween(Float rangeStart, Float rangeEnd);

    /**
     * 根据物品高度查询物品
     *
     * @param height   物品高度
     * @param pageable 分页
     * @return 物品列表
     */
    List<Item> findAllByHeight(Float height, Pageable pageable);

    /**
     * 根据物品高度查询物品数量
     *
     * @param height 物品高度
     * @return 物品数量
     */
    long countAllByHeight(Float height);

    /**
     * 根据物品高度范围查询物品
     *
     * @param rangeStart 范围开始
     * @param rangeEnd   范围结束
     * @param pageable   分页
     * @return 物品列表
     */
    List<Item> findAllByHeightBetween(Float rangeStart, Float rangeEnd, Pageable pageable);

    /**
     * 根据物品高度范围查询物品数量
     *
     * @param rangeStart 范围开始
     * @param rangeEnd   范围结束
     * @return 物品数量
     */
    long countAllByHeightBetween(Float rangeStart, Float rangeEnd);

    /**
     * 根据物品架构模糊查询物品
     *
     * @param architecture 物品架构
     * @param pageable     分页
     * @return 物品列表
     */
    List<Item> findAllByArchitectureContaining(String architecture, Pageable pageable);

    /**
     * 根据物品架构模糊查询物品数量
     *
     * @param architecture 物品架构
     * @return 物品数量
     */
    long countAllByArchitectureContaining(String architecture);
}
