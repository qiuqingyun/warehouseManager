package com.qing98.warehouseManager;

import com.qing98.warehouseManager.entity.Owner;
import com.qing98.warehouseManager.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * @author QIU
 */
@SpringBootApplication
public class WarehouseManagerApplication {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseManagerApplication.class.getName());
    public static void main(String[] args) {
        SpringApplication.run(WarehouseManagerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(OwnerRepository repository) {
        return (args) -> {
            // save a few customers
            logger.info("Save new Owner");
            logger.info("-------------------------------");
            Owner jack = new Owner("Jack2");
            repository.save(jack);
            repository.save(new Owner("Bob2"));

            // fetch all customers
            logger.info("Owners found with findAll():");
            logger.info("-------------------------------");
            for (Owner owner : repository.findAll()) {
                logger.info(owner.toString());
            }

            jack.setPhoneNumber("18682923417");
            repository.save(jack);
            Optional<Owner> owner = repository.findById(jack.getId());
            owner.ifPresent(value -> logger.info(value.toString()));
        };
    }
}
