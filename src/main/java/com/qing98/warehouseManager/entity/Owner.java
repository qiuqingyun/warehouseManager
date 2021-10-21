package com.qing98.warehouseManager.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author QIU
 */
@Entity
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;
    private LocalDateTime dateRegistration;

    protected Owner() {
    }

    public Owner(String name) {
        this.name = name;
    }

    public Owner(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDateTime getDateRegistration() {
        return dateRegistration;
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
