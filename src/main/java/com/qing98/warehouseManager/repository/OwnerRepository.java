package com.qing98.warehouseManager.repository;

import com.qing98.warehouseManager.entity.Item;
import com.qing98.warehouseManager.entity.Owner;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author QIU
 */
public interface OwnerRepository extends JpaRepository<Owner, Long> {
    /**
     * 根据所有者名称模糊查询所有者，不分页
     *
     * @param name 所有者名称
     * @return 所有者列表
     */
    List<Owner> findAllByNameContaining(String name);

    /**
     * 根据所有者名称模糊查询所有者
     *
     * @param name     所有者名称
     * @param pageable 分页
     * @return 所有者列表
     */
    List<Owner> findAllByNameContaining(String name, Pageable pageable);

    /**
     * 根据所有者名称模糊查询所有者数量
     *
     * @param name 所有者名称
     * @return 所有者数量
     */
    long countAllByNameContaining(String name);

    /**
     * 根据所有者电话号码查询所有者
     *
     * @param phoneNumber 所有者电话号码
     * @param pageable    分页
     * @return 所有者列表
     */
    List<Owner> findAllByPhoneNumber(String phoneNumber, Pageable pageable);

    /**
     * 根据所有者电话号码查询所有者数量
     *
     * @param phoneNumber 所有者电话号码
     * @return 所有者数量
     */
    long countAllByPhoneNumber(String phoneNumber);

    /**
     * 根据所有者注册时间查询所有者
     *
     * @param dateRegistration 所有者注册时间
     * @param pageable         分页
     * @return 所有者列表
     */
    List<Owner> findAllByDateRegistration(LocalDateTime dateRegistration, Pageable pageable);

    /**
     * 根据所有者注册时间查询所有者数量
     *
     * @param dateRegistration 所有者注册时间
     * @return 所有者数量
     */
    long countAllByDateRegistration(LocalDateTime dateRegistration);

    /**
     * 根据所有者注册时间范围查询所有者
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @param pageable  分页
     * @return 所有者列表
     */
    List<Owner> findAllByDateRegistrationBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    /**
     * 根据所有者注册时间范围查询所有者数量
     *
     * @param dateStart 范围开始时间
     * @param dateEnd   范围结束时间
     * @return 所有者数量
     */
    long countAllByDateRegistrationBetween(LocalDateTime dateStart, LocalDateTime dateEnd);

    /**
     * 根据所有者备注模糊查询所有者
     *
     * @param note     所有者备注
     * @param pageable 分页
     * @return 所有者列表
     */
    List<Owner> findAllByNoteContaining(String note, Pageable pageable);

    /**
     * 根据所有者备注模糊查询所有者数量
     *
     * @param note 所有者备注
     * @return 所有者数量
     */
    long countAllByNoteContaining(String note);
}
