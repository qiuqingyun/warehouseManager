package com.qing98.warehouseManager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qing98.warehouseManager.entity.Item;
import com.qing98.warehouseManager.entity.Owner;
import com.qing98.warehouseManager.repository.ItemRepository;
import com.qing98.warehouseManager.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author QIU
 */
@Controller
public class WarehouseController {
    private final static Logger logger = LoggerFactory.getLogger(WarehouseController.class.getName());
    private final ObjectMapper mapper = new ObjectMapper();

    private final OwnerRepository ownerRepository;
    private final ItemRepository itemRepository;

    public WarehouseController(OwnerRepository ownerRepository, ItemRepository itemRepository) {
        this.ownerRepository = ownerRepository;
        this.itemRepository = itemRepository;
    }

    /**
     * 添加新的所有者信息
     *
     * @param owner 所有者信息
     * @return 已添加的所有者信息，包括系统分配的所有者id以及注册时间
     */
    @PostMapping(path = "/owner/add")
    public ResponseEntity<String> addNewOwner(@RequestBody Owner owner) {
        if (owner.check()) {
            ownerRepository.save(owner);
            logger.info("Insert new Owner [" + owner.getId() + ":" + owner.getName() + "]");
            return new Response().success(owner);
        } else {
            return new Response().badRequest();
        }
    }

    /**
     * 获取所有的所有者信息
     *
     * @return 所有者信息数组
     */
    @GetMapping(path = "/owner/get/all")
    public ResponseEntity<String> getAllOwner(@RequestParam int page, @RequestParam int limit) {
        page -= 1;
        Pageable pageable = PageRequest.of(page, limit);
        List<Owner> resultList = ownerRepository.findAll(pageable).toList();
        //提取所有者ID，名称信息
        List<OwnersTableItem> ownersTableItems = new ArrayList<>();
        for (Owner owner : resultList) {
            ownersTableItems.add(new OwnersTableItem(owner));
        }
        OwnersTable ownersTable = new OwnersTable(ownersTableItems, ownerRepository.count());
        return new Response().success(ownersTable);
    }

    /**
     * 根据id获取指定的所有者信息
     *
     * @param id 所有者id
     * @return 所有者信息
     */
    @GetMapping(path = "/owner/get")
    public ResponseEntity<String> getOwnerById(@RequestParam String id) {
        Optional<Owner> owner = ownerRepository.findById(Long.parseLong(id));
        if (owner.isPresent()) {
            return new Response().success(owner.get());
        } else {
            return new Response().notFound();
        }
    }

    /**
     * 添加新的物品的元数据
     *
     * @param item 物品元数据
     * @return 物品元数据
     */
    @PostMapping(path = "/item/add")
    public ResponseEntity<String> addNewItem(@RequestBody Item item) {
        //检查所有者的正确性
        boolean ownerCheck = true;
        if (item.getOwnerId() != null) {
            Optional<Owner> owner = ownerRepository.findById(item.getOwnerId());
            ownerCheck = owner.isPresent();
            if (!ownerCheck) {
                logger.warn("Add new item failed: no owner " + item.getOwnerId());
            }
        }
        if (item.check() && ownerCheck) {
            itemRepository.save(item);
            logger.info("Insert new Item [" + item.getUuid() + ":" + item.getName() + "]");
            return new Response().success(item);
        } else {
            return new Response().badRequest();
        }
    }

    /**
     * 物品状态变更
     *
     * @param uuid 物品编号
     * @return 物品的信息
     */
    @PostMapping(path = "/item/move")
    public ResponseEntity<String> itemOut(@RequestParam String uuid, @RequestParam String act) {
        Optional<Item> itemTemp = itemRepository.findById(UUID.fromString(uuid));
        Item.Act itemAct = Item.Act.valueOf(act);
        if (itemTemp.isPresent()) {
            Item item = itemTemp.get();
            if (itemAct.equals(Item.Act.into)) {
                item.into();
            } else if (itemAct.equals(Item.Act.leave)) {
                item.leave();
            }
            itemRepository.save(item);
            return new Response().success(item);
        } else {
            return new Response().notFound();
        }
    }

    /**
     * 为物品添加所有者
     *
     * @param uuid 物品编号
     * @param id   所有者编号
     * @return 物品信息
     */
    @PostMapping(path = "/item/addOwner")
    public ResponseEntity<String> itemAddOwner(@RequestParam String uuid, @RequestParam String id) {
        Optional<Item> itemTemp = itemRepository.findById(UUID.fromString(uuid));
        if (itemTemp.isPresent()) {
            Item item = itemTemp.get();
            item.setOwnerId(Long.valueOf(id));
            itemRepository.save(item);
            return new Response().success(item);
        } else {
            return new Response().notFound();
        }
    }

    /**
     * 获取所有的物品元数据
     *
     * @return 物品元数据数组
     */
    @GetMapping(path = "/item/get/all")
    public ResponseEntity<String> getAllItem() {
        return new Response().success(itemRepository.findAll(Sort.by(Sort.Direction.DESC, "dateRecord")));
    }

    /**
     * 获取所有指定状态的物品元数据
     *
     * @param status 物品状态
     * @param page   页数
     * @param limit  条数
     * @return 物品元数据数组
     */
    @GetMapping(path = "/item/get/status")
    public ResponseEntity<String> getItemByStatus(@RequestParam String status, @RequestParam int page, @RequestParam int limit) {
        page -= 1;
        Sort sort;
        Item.Status itemStatus = Item.Status.valueOf(status);
        if (itemStatus.equals(Item.Status.order)) {
            sort = Sort.by(Sort.Direction.DESC, "dateRecord");
        } else if (itemStatus.equals(Item.Status.keep)) {
            sort = Sort.by(Sort.Direction.DESC, "dateInto");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "dateLeave");
        }
        Pageable pageable = PageRequest.of(page, limit, sort);
        List<Item> resultList = itemRepository.findAllByStatus(itemStatus, pageable);
        //提取物品UUID，名称以及日期等信息
        List<ItemsTableItem> itemsTableItems = new ArrayList<>();
        for (Item item : resultList) {
            itemsTableItems.add(new ItemsTableItem(item));
        }
        ItemsTable itemsTable = new ItemsTable(itemsTableItems, itemRepository.countAllByStatus(Item.Status.valueOf(status)));
        return new Response().success(itemsTable);
    }

    /**
     * 根据物品uuid获取其元数据
     *
     * @param uuid 物品uuid
     * @return 物品元数据
     */
    @GetMapping(path = "/item/get")
    public ResponseEntity<String> getItemByUuid(@RequestParam String uuid) {
        Optional<Item> itemMetadata = itemRepository.findById(UUID.fromString(uuid));
        if (itemMetadata.isPresent()) {
            return new Response().success(itemMetadata.get());
        } else {
            return new Response().notFound();
        }
    }

    class Response {
        public ResponseEntity<String> success(Object object) {
            try {
                return new ResponseEntity<>(mapper.writeValueAsString(object), HttpStatus.OK);
            } catch (JsonProcessingException e) {
                logger.warn("Parse json failed: " + e);
                e.printStackTrace();
                return error(e.getMessage());
            }
        }

        public ResponseEntity<String> notFound() {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        public ResponseEntity<String> badRequest() {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        public ResponseEntity<String> error(String error) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    static class ItemsTableItem {
        public String uuid;
        public String name;
        public String dateRecord;
        public String dateInto;
        public String dateLeave;

        public ItemsTableItem(Item item) {
            this.uuid = item.getUuid().toString();
            this.name = item.getName();
            this.dateRecord = item.getDateRecord();
            this.dateInto = item.getDateInto();
            this.dateLeave = item.getDateLeave();
        }
    }

    static class ItemsTable {
        public int code = 0;
        public List<ItemsTableItem> data;
        public String msg = "";
        public long count;

        public ItemsTable(List<ItemsTableItem> data, long count) {
            this.data = data;
            this.count = count;
        }
    }

    static class OwnersTableItem {
        public String id;
        public String name;
        public String note;

        public OwnersTableItem(Owner owner) {
            this.id = String.valueOf(owner.getId());
            this.name = owner.getName();
            this.note = owner.getNote();
        }
    }

    static class OwnersTable {
        public int code = 0;
        public List<OwnersTableItem> data;
        public String msg = "";
        public long count;

        public OwnersTable(List<OwnersTableItem> data, long count) {
            this.data = data;
            this.count = count;
        }
    }
}

