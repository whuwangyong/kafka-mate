package cn.whu.wy.kafkamate.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author WangYong
 * Date 2023/03/13
 * Time 09:44
 */
public class Utils {

    public static final Duration TIMEOUT_MS_10 = Duration.ofMillis(10);
    public static final Duration TIMEOUT_MS_100 = Duration.ofMillis(100);
    public static final Duration TIMEOUT_MS_1000 = Duration.ofMillis(1000);

    // eg: 0315134842565018
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMddHHmmssSSSSSS");

    public static String getTimestamp() {
        return FORMATTER.format(LocalDateTime.now());
    }
}
