package cn.whu.wy.kafkamate;

import cn.whu.wy.kafkamate.service.Utils;
import org.junit.jupiter.api.Test;

/**
 * @author WangYong
 * Date 2023/03/14
 * Time 08:47
 */
public class SimpleTest {
    @Test
    public void t1() {
        System.out.println(Utils.getTimestamp());
    }
}
