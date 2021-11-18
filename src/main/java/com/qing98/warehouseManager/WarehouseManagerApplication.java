package com.qing98.warehouseManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author QIU
 */
@SpringBootApplication
public class WarehouseManagerApplication {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseManagerApplication.class.getName());
    public static void main(String[] args) {
        SpringApplication.run(WarehouseManagerApplication.class, args);
    }
}
