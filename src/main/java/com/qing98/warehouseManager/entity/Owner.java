package com.qing98.warehouseManager.entity;

import com.qing98.warehouseManager.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private LocalDateTime dateLastChange;

    protected Owner() {
    }

    public boolean checkInsert() {
        if (name == null || name.isBlank()) {
            logger.warn("Insert Owner failed: no name");
            return false;
        }
        return true;
    }

    public boolean checkEdit() {
        if (name == null || name.isBlank()) {
            logger.warn("Edit Owner failed: no name");
            return false;
        }
        if (dateRegistration == null) {
            logger.warn("Edit Owner failed: no dateRegistration");
            return false;
        }
        return true;
    }

    public boolean isIdNull() {
        return id==null;
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
    public void setDates() {
        if (this.dateRegistration == null) {
            this.dateRegistration = LocalDateTime.now();
        }
        this.dateLastChange=LocalDateTime.now();
    }

    public void setDateRegistration(String dateRegistration) {
        if (!dateRegistration.isBlank()) {
            this.dateRegistration = LocalDateTime.parse(dateRegistration, Config.FORMATTER);
        } else {
            this.dateRegistration = LocalDateTime.now();
        }
    }

    public void edit() {
        this.dateLastChange = LocalDateTime.now();
    }

    public String getDateLastChange() {
        return dateLastChange.format(Config.FORMATTER);
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
