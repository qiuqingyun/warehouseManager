package com.qing98.warehouseManager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qing98.warehouseManager.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author QIU
 */
@Entity
public class Owner {
    private final static Logger logger = LoggerFactory.getLogger(Owner.class.getName());
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private String note;
    private LocalDateTime dateRegistration;

    protected Owner() {
    }

    public boolean check() {
        if (name == null || name.isBlank()) {
            logger.warn("Owner check failed: no name");
            return false;
        }
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDateRegistration() {
        return dateRegistration.format(Config.FORMATTER);
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @PrePersist
    public void setDateRegistration() {
        this.dateRegistration = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "\nOwner{\n" +
                "  id=" + id + '\n' +
                "  name='" + name + "'\n" +
                "  phoneNumber='" + phoneNumber + "'\n" +
                "  dateRegistration=" + dateRegistration + '\n' +
                '}';
    }
}
