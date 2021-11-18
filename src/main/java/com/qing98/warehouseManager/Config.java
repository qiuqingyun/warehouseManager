package com.qing98.warehouseManager;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @author QIU
 */
public class Config {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
}
