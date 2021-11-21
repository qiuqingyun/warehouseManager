package com.qing98.warehouseManager.entity;

import com.qing98.warehouseManager.Config;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author QIU
 */
@Entity
public class Item {
    private final static Logger logger = LoggerFactory.getLogger(Item.class.getName());

    protected Item() {
    }

    /**
     * 物品状态值
     */
    public enum Status {
        /**
         * 订购状态，还未入库
         */
        order,
        /**
         * 在库状态，已入库
         */
        keep,
        /**
         * 出库状态，已出库
         */
        export
    }

    /**
     * 物品状态变更
     */
    public enum Act {
        /**
         * 订购状态，入库
         */
        into,
        /**
         * 在库状态，出库
         */
        leave
    }

    /**
     * 物品编号
     */
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Type(type = "uuid-char")
    private UUID uuid = null;
    /**
     * 物品名称
     */
    private String name = null;
    /**
     * 物品所有者
     */
    private Long ownerId = null;
    /**
     * 物品状态
     */
    private Status status = Status.order;
    /**
     * 物品登记时间
     */
    private LocalDateTime dateRecord = null;
    /**
     * 物品入库时间
     */
    private LocalDateTime dateInto = null;
    /**
     * 物品出库时间
     */
    private LocalDateTime dateLeave = null;
    /**
     * 物品尺寸之长
     */
    private Float length = 0F;
    /**
     * 物品尺寸之宽
     */
    private Float width = 0F;
    /**
     * 物品尺寸之高
     */
    private Float height = 0F;
    /**
     * 物品架构
     */
    private String architecture = "x86";
    /**
     * 物品描述
     */
    private String description;

    /**
     * 检查元数据正确性
     *
     * @return 正确性
     */
    public boolean check() {
        //名称正确性
        if (name == null || name.isBlank()) {
            logger.warn("Item check failed: no name");
            return false;
        }
        return true;
    }

    /**
     * 处于订购状态的物品入库
     *
     * @return 入库结果
     */
    public boolean into() {
        if (this.status != Status.order) {
            logger.warn("Set item [" + this.uuid + ": " + this.name + "] Status Keep failed: status not in Order");
            return false;
        }
        this.status = Status.keep;
        this.dateInto = LocalDateTime.now();
        logger.info("Item [" + this.uuid + ": " + this.name + "] New Status: Keep");
        return true;
    }

    /**
     * 处于在库状态的物品出库
     *
     * @return 出库结果
     */
    public boolean leave() {
        //检测状态是否为在库
        if (this.status != Status.keep) {
            logger.warn("Set item [" + this.uuid + ": " + this.name + "] Status Export failed: status not in Keep");
            return false;
        }
        //检测是否有所有者
        if (this.ownerId == null) {
            logger.warn("Set item [" + this.uuid + ": " + this.name + "] LEAVE failed: no ownerId");
            return false;
        }
        this.status = Status.export;
        this.dateLeave = LocalDateTime.now();
        logger.info("Item [" + this.uuid + ": " + this.name + "] New Status: Export");
        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public String getDateRecord() {
        return dateRecord.format(Config.FORMATTER);
    }

    public LocalDateTime compare() {
        return dateRecord;
    }

    @PrePersist
    public void setDateRecord() {
        this.dateRecord = LocalDateTime.now();
    }

    public void setDateInto(String dateInto) {
        if (!dateInto.isBlank()) {
            this.dateInto = LocalDateTime.parse(dateInto, Config.FORMATTER);
        }
    }

    public String getDateInto() {
        if (dateInto != null) {
            return dateInto.format(Config.FORMATTER);
        } else {
            return null;
        }
    }

    public void setDateLeave(String dateLeave) {
        if (!dateLeave.isBlank()) {
            this.dateLeave = LocalDateTime.parse(dateLeave, Config.FORMATTER);
        }
    }

    public String getDateLeave() {
        if (dateLeave != null) {
            return dateLeave.format(Config.FORMATTER);
        } else {
            return null;
        }
    }

    public Float getLength() {
        return length;
    }

    public void setLength(Float length) {
        this.length = length;
    }

    public Float getWidth() {
        return width;
    }

    public void setWidth(Float width) {
        this.width = width;
    }

    public Float getHeight() {
        return height;
    }

    public void setHeight(Float height) {
        this.height = height;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "\nItem{\n" +
                "  uuid: " + uuid + '\n' +
                "  name: '" + name + "'\n" +
                "  ownerId: " + ownerId + '\n' +
                "  status: " + status + '\n' +
                "  dateRecord: " + dateRecord + '\n' +
                "  dateInto: " + dateInto + '\n' +
                "  dateLeave: " + dateLeave + '\n' +
                "  length: " + length + '\n' +
                "  width: " + width + '\n' +
                "  height: " + height + '\n' +
                "  architecture: '" + architecture + "'\n" +
                "  description: '" + description + "'\n" +
                '}';
    }
}
