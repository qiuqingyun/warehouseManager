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
    List<Owner> findAllByNameContaining(String name);

    List<Owner> findAllByNameContaining(String name, Pageable pageable);

    long countAllByNameContaining(String name);

    List<Owner> findAllByPhoneNumber(String phoneNumber, Pageable pageable);

    long countAllByPhoneNumber(String phoneNumber);

    List<Owner> findAllByDateRegistration(LocalDateTime dateRegistration, Pageable pageable);

    long countAllByDateRegistration(LocalDateTime dateRegistration);

    List<Owner> findAllByDateRegistrationBetween(LocalDateTime dateStart, LocalDateTime dateEnd, Pageable pageable);

    long countAllByDateRegistrationBetween(LocalDateTime dateStart, LocalDateTime dateEnd);

    List<Owner> findAllByNoteContaining(String note, Pageable pageable);

    long countAllByNoteContaining(String note);
}
