package com.qing98.warehouseManager.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qing98.warehouseManager.Config;
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

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
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
        if (owner.checkInsert()) {
            ownerRepository.save(owner);
            logger.info("Insert new Owner [" + owner.getId() + ":" + owner.getName() + "]");
            return new Response().success(owner);
        } else {
            return new Response().badRequest();
        }
    }

    @PostMapping(path = "/owner/edit")
    public ResponseEntity<String> editOwner(@RequestBody Owner owner) {
        if (owner != null) {
            if (!owner.isIdNull()) {
                long id = owner.getId();
                Optional<Owner> ownerOpt = ownerRepository.findById(id);
                if (ownerOpt.isPresent()) {
                    if (owner.checkEdit()) {
                        if (ownerOpt.get().getDateRegistration().equals(owner.getDateRegistration())) {
                            ownerRepository.save(owner);
                            logger.info("Edit Owner [" + owner.getId() + "]");
                            return new Response().success(owner);
                        } else {
                            logger.warn("Edit Owner failed: dateRegistration changed");
                        }
                    }
                } else {
                    logger.warn("Edit Owner failed: no such owner: " + id);
                }
            } else {
                logger.warn("Edit Owner failed: no id");
            }
        } else {
            logger.warn("Edit Owner failed: Request Error");
        }
        return new Response().badRequest();
    }

    /**
     * 根据指定的条件筛选所有者
     *
     * @param page      页数
     * @param limit     每页的数量
     * @param word      关键词1
     * @param word2     关键词2
     * @param condition 筛选条件
     * @return 所有者列表
     */
    @GetMapping(path = "/owner/get/condition")
    public ResponseEntity<String> getOwnerByCondition(@RequestParam int page, @RequestParam int limit,
                                                      @RequestParam String word, @RequestParam(defaultValue = "") String word2,
                                                      @RequestParam String condition) {
        page -= 1;
        List<Owner> resultList;
        long total = 0;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateRegistration"));
        switch (condition) {
            //根据所有者名称查找
            case "name": {
                //查询
                resultList = ownerRepository.findAllByNameContaining(word, pageable);
                //计数
                total = ownerRepository.countAllByNameContaining(word);
                break;
            }
            //根据所有者编号查找
            case "id": {
                try {
                    Long id = Long.parseLong(word);
                    //查询
                    Optional<Owner> owner = ownerRepository.findById(id);
                    resultList = new ArrayList<>(1);
                    if (owner.isPresent()) {
                        resultList.add(owner.get());
                        //计数
                        total = 1;
                    }
                } catch (IllegalArgumentException e) {
                    //编号格式错误
                    logger.warn("Parse Long[id] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据所有者电话查找
            case "phoneNumber": {
                //查询
                resultList = ownerRepository.findAllByPhoneNumber(word, pageable);
                //计数
                total = ownerRepository.countAllByPhoneNumber(word);
                break;
            }
            //根据所有者登记时间查找
            case "dateRegistration": {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(word, Config.FORMATTER);
                    resultList = ownerRepository.findAllByDateRegistration(dateTime, pageable);
                    total = ownerRepository.countAllByDateRegistration(dateTime);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateRegistration] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据所有者登记时间范围查找
            case "dateRegistrationRange": {
                try {
                    LocalDateTime dateTimeStart = LocalDateTime.parse(word, Config.FORMATTER);
                    LocalDateTime dateTimeEnd = LocalDateTime.parse(word2, Config.FORMATTER);
                    resultList = ownerRepository.findAllByDateRegistrationBetween(dateTimeStart, dateTimeEnd, pageable);
                    total = ownerRepository.countAllByDateRegistrationBetween(dateTimeStart, dateTimeEnd);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateRegistration] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据所有者备注查找
            case "note": {
                resultList = ownerRepository.findAllByNoteContaining(word, pageable);
                total = ownerRepository.countAllByNoteContaining(word);
                break;
            }
            default: {
                return new Response().badRequest();
            }
        }
        //提取所有者ID等信息
        List<OwnersTableItem> ownersTableItems = new ArrayList<>();
        for (Owner owner : resultList) {
            ownersTableItems.add(new OwnersTableItem(owner));
        }
        OwnersTable ownersTable = new OwnersTable(ownersTableItems, total);
        return new Response().success(ownersTable);
    }

    /**
     * 获取所有的所有者信息
     *
     * @return 所有者信息数组
     */
    @GetMapping(path = "/owner/get/all")
    public ResponseEntity<String> getAllOwner(@RequestParam int page, @RequestParam int limit) {
        page -= 1;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateRegistration"));
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

    /**
     * 获取所有指定状态的物品元数据
     *
     * @param status 物品状态
     * @param page   页数
     * @param limit  条数
     * @return 物品元数据数组
     */
    @GetMapping(path = "/item/get/status")
    public ResponseEntity<String> getItemByStatus(@RequestParam int page, @RequestParam int limit,
                                                  @RequestParam String status) {
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
     * 根据指定的条件筛选物品
     *
     * @param page      页数
     * @param limit     每页的数量
     * @param word      关键词1
     * @param word2     关键词2
     * @param condition 筛选条件
     * @return 物品列表
     */
    @GetMapping(path = "/item/get/condition")
    public ResponseEntity<String> getItemByCondition(@RequestParam int page, @RequestParam int limit,
                                                     @RequestParam String word, @RequestParam(defaultValue = "") String word2,
                                                     @RequestParam String condition) {
        page -= 1;
        List<Item> resultList;
        long total = 0;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateRecord"));
        switch (condition) {
            //根据物品名称查找
            case "name": {
                //查询
                resultList = itemRepository.findAllByNameContaining(word, pageable);
                //计数
                total = itemRepository.countAllByNameContaining(word);
                break;
            }
            //根据物品编号查找
            case "uuid": {
                try {
                    UUID uuid = UUID.fromString(word);
                    //查询
                    Optional<Item> item = itemRepository.findById(uuid);
                    resultList = new ArrayList<>(1);
                    if (item.isPresent()) {
                        resultList.add(item.get());
                        //计数
                        total = 1;
                    }
                } catch (IllegalArgumentException e) {
                    //编号格式错误
                    logger.warn("Parse UUID[uuid] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品所有者名称查找
            case "owner": {
                //查询对应的所有者
                List<Owner> owners = ownerRepository.findAllByNameContaining(word);
                List<Item> totalList = new ArrayList<>();
                for (Owner owner : owners) {
                    long ownerId = owner.getId();
                    totalList.addAll(itemRepository.findAllByOwnerId(ownerId));
                }
                //排序
                totalList.sort(Comparator.comparing(Item::compareDateRecord));
                //分页
                int pageStart = Math.min(page * limit, totalList.size());
                int pageEnd = Math.min((page + 1) * limit, totalList.size());
                resultList = new ArrayList<>(totalList.subList(pageStart, pageEnd));
                //计数
                total = totalList.size();
                break;
            }
            //根据物品所有者编号查找
            case "ownerId": {
                try {
                    //查询对应的所有者
                    Long ownerId = Long.parseLong(word);
                    Optional<Owner> owner = ownerRepository.findById(ownerId);
                    if (owner.isPresent()) {
                        //当所有者存在时
                        resultList = itemRepository.findAllByOwnerId(ownerId, pageable);
                        //计数
                        total = itemRepository.countAllByOwnerId(ownerId);
                    } else {
                        resultList = new ArrayList<>();
                    }
                } catch (NumberFormatException e) {
                    //编号格式错误
                    logger.warn("Parse Long[ownerId] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品登记时间查找
            case "dateRecord": {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(word, Config.FORMATTER);
                    resultList = itemRepository.findAllByDateRecord(dateTime, pageable);
                    total = itemRepository.countAllByDateRecord(dateTime);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateRecord] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品登记时间范围查找
            case "dateRecordRange": {
                try {
                    LocalDateTime dateTimeStart = LocalDateTime.parse(word, Config.FORMATTER);
                    LocalDateTime dateTimeEnd = LocalDateTime.parse(word2, Config.FORMATTER);
                    resultList = itemRepository.findAllByDateRecordBetween(dateTimeStart, dateTimeEnd, pageable);
                    total = itemRepository.countAllByDateRecordBetween(dateTimeStart, dateTimeEnd);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateRecord] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品入库时间查找
            case "dateInto": {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(word, Config.FORMATTER);
                    Pageable pageableTemp = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateInto"));
                    resultList = itemRepository.findAllByDateInto(dateTime, pageableTemp);
                    total = itemRepository.countAllByDateInto(dateTime);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateInto] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品入库时间范围查找
            case "dateIntoRange": {
                try {
                    LocalDateTime dateTimeStart = LocalDateTime.parse(word, Config.FORMATTER);
                    LocalDateTime dateTimeEnd = LocalDateTime.parse(word2, Config.FORMATTER);
                    Pageable pageableTemp = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateInto"));
                    resultList = itemRepository.findAllByDateIntoBetween(dateTimeStart, dateTimeEnd, pageableTemp);
                    total = itemRepository.countAllByDateIntoBetween(dateTimeStart, dateTimeEnd);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateInto] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品出库时间查找
            case "dateLeave": {
                try {
                    LocalDateTime dateTime = LocalDateTime.parse(word, Config.FORMATTER);
                    Pageable pageableTemp = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateLeave"));
                    resultList = itemRepository.findAllByDateLeave(dateTime, pageableTemp);
                    total = itemRepository.countAllByDateLeave(dateTime);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateLeave] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品出库时间范围查找
            case "dateLeaveRange": {
                try {
                    LocalDateTime dateTimeStart = LocalDateTime.parse(word, Config.FORMATTER);
                    LocalDateTime dateTimeEnd = LocalDateTime.parse(word2, Config.FORMATTER);
                    Pageable pageableTemp = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateLeave"));
                    resultList = itemRepository.findAllByDateLeaveBetween(dateTimeStart, dateTimeEnd, pageableTemp);
                    total = itemRepository.countAllByDateLeaveBetween(dateTimeStart, dateTimeEnd);
                } catch (DateTimeParseException e) {
                    logger.warn("Parse DateTime[dateLeave] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品描述查找
            case "description": {
                resultList = itemRepository.findAllByDescriptionContaining(word, pageable);
                total = itemRepository.countAllByDescriptionContaining(word);
                break;
            }
            //根据物品尺寸之长查找
            case "length": {
                try {
                    Float length = Float.valueOf(word);
                    resultList = itemRepository.findAllByLength(length, pageable);
                    total = itemRepository.countAllByLength(length);
                } catch (NumberFormatException e) {
                    logger.warn("Parse Float[length] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品尺寸之长的范围查找
            case "lengthRange": {
                try {
                    Float rangeStart = Float.valueOf(word);
                    Float rangeEnd = Float.valueOf(word2);
                    resultList = itemRepository.findAllByLengthBetween(rangeStart, rangeEnd, pageable);
                    total = itemRepository.countAllByLengthBetween(rangeStart, rangeEnd);
                } catch (NumberFormatException e) {
                    logger.warn("Parse Float[length] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品尺寸之宽查找
            case "width": {
                try {
                    Float width = Float.valueOf(word);
                    Pageable pageableTemp = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateInto"));
                    resultList = itemRepository.findAllByWidth(width, pageableTemp);
                    total = itemRepository.countAllByWidth(width);
                } catch (NumberFormatException e) {
                    logger.warn("Parse Float[width] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品尺寸之宽的范围查找
            case "widthRange": {
                try {
                    Float rangeStart = Float.valueOf(word);
                    Float rangeEnd = Float.valueOf(word2);
                    resultList = itemRepository.findAllByWidthBetween(rangeStart, rangeEnd, pageable);
                    total = itemRepository.countAllByWidthBetween(rangeStart, rangeEnd);
                } catch (NumberFormatException e) {
                    logger.warn("Parse Float[width] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品尺寸之高查找
            case "height": {
                try {
                    Float height = Float.valueOf(word);
                    Pageable pageableTemp = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateLeave"));
                    resultList = itemRepository.findAllByHeight(height, pageableTemp);
                    total = itemRepository.countAllByHeight(height);
                } catch (NumberFormatException e) {
                    logger.warn("Parse Float[height] failed: " + word);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品尺寸之高的范围查找
            case "heightRange": {
                try {
                    Float rangeStart = Float.valueOf(word);
                    Float rangeEnd = Float.valueOf(word2);
                    resultList = itemRepository.findAllByHeightBetween(rangeStart, rangeEnd, pageable);
                    total = itemRepository.countAllByHeightBetween(rangeStart, rangeEnd);
                } catch (NumberFormatException e) {
                    logger.warn("Parse Float[height] failed: " + word + ", " + word2);
                    resultList = new ArrayList<>();
                }
                break;
            }
            //根据物品架构查找
            case "architecture": {
                resultList = itemRepository.findAllByArchitectureContaining(word, pageable);
                total = itemRepository.countAllByArchitectureContaining(word);
                break;
            }

            default: {
                return new Response().badRequest();
            }
        }
        //提取物品UUID，名称以及日期等信息
        List<ItemsTableItem> itemsTableItems = new ArrayList<>();
        for (Item item : resultList) {
            itemsTableItems.add(new ItemsTableItem(item));
        }
        ItemsTable itemsTable = new ItemsTable(itemsTableItems, total);
        return new Response().success(itemsTable);
    }

    /**
     * 获取所有的物品元数据
     *
     * @return 物品元数据数组
     */
    @GetMapping(path = "/item/get/all")
    public ResponseEntity<String> getAllItem(@RequestParam int page, @RequestParam int limit) {
        page -= 1;
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "dateRecord"));
        List<Item> items = itemRepository.findAll(pageable).toList();
        //提取物品UUID，名称以及日期等信息
        List<ItemsTableItem> itemsTableItems = new ArrayList<>();
        for (Item item : items) {
            itemsTableItems.add(new ItemsTableItem(item));
        }
        ItemsTable itemsTable = new ItemsTable(itemsTableItems, itemRepository.count());
        return new Response().success(itemsTable);
    }

    /**
     * 按小时获取各状态下物品数量
     *
     * @param limit 时间点数量
     * @return 数量数组
     */
    @GetMapping(path = "/item/get/quantity")
    public ResponseEntity<String> getItemsQuantity(@RequestParam int limit) {
        List<Item> items = itemRepository.findAll();
        LocalDateTime localDateTime = LocalDateTime.now().with(LocalTime.MAX);
        String[] dateTimes = new String[limit];
        Long[] orders = new Long[limit];
        Long[] keeps = new Long[limit];
        Long[] exports = new Long[limit];
        for (int i = 0; i < limit; i++) {
            long order = countOrders(items, localDateTime);
            long keep = countKeeps(items, localDateTime);
            long export = countExports(items, localDateTime);
            int index = limit - i - 1;
            dateTimes[index] = localDateTime.format(Config.FORMATTER_DAY);
            orders[index] = order;
            keeps[index] = keep;
            exports[index] = export;
            localDateTime = localDateTime.minusDays(1);
        }
        Quantity quantity = new Quantity(dateTimes, orders, keeps, exports);
        return new Response().success(quantity);
    }

    /**
     * 遍历所有物品中，在当前时间点下，处于订购状态的物品数量
     *
     * @param items         物品列表
     * @param localDateTime 时间点
     * @return 物品数量
     */
    private long countOrders(List<Item> items, LocalDateTime localDateTime) {
        long counts = 0;
        for (Item item : items) {
            if (localDateTime.isAfter(item.compareDateRecord()) &&
                    (item.isDateIntoNull() || localDateTime.isBefore(item.compareDateInto()))) {
                counts++;
            }
        }
        return counts;
    }

    /**
     * 遍历所有物品中，在当前时间点下，处于库存状态的物品数量
     *
     * @param items         物品列表
     * @param localDateTime 时间点
     * @return 物品数量
     */
    private long countKeeps(List<Item> items, LocalDateTime localDateTime) {
        long counts = 0;
        for (Item item : items) {
            if (!item.isDateIntoNull()) {
                if (localDateTime.isAfter(item.compareDateInto())) {
                    if (item.isDateLeaveNull() || localDateTime.isBefore(item.compareDateLeave())) {
                        counts++;
                    }
                }
            }
        }
        return counts;
    }

    /**
     * 遍历所有物品中，在当前时间点下，处于出库状态的物品数量
     *
     * @param items         物品列表
     * @param localDateTime 时间点
     * @return 物品数量
     */
    private long countExports(List<Item> items, LocalDateTime localDateTime) {
        long counts = 0;
        for (Item item : items) {
            if (!item.isDateLeaveNull()) {
                if (localDateTime.isAfter(item.compareDateLeave())) {
                    counts++;
                }
            }
        }
        return counts;
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

    static class Quantity {
        public String[] dateTimes;
        public Long[] orders;
        public Long[] keeps;
        public Long[] exports;

        public Quantity(String[] dateTimes, Long[] orders, Long[] keeps, Long[] exports) {
            this.dateTimes = dateTimes;
            this.orders = orders;
            this.keeps = keeps;
            this.exports = exports;
        }
    }
}


