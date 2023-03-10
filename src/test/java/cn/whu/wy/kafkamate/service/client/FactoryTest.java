package cn.whu.wy.kafkamate.service.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author WangYong
 * Date 2023/03/10
 * Time 10:57
 */
@SpringBootTest
@Slf4j
public class FactoryTest {
    @Autowired
    ProducerFactory producerFactory;

    @Autowired
    ConsumerFactory consumerFactory;

    @Test
    public void test() throws InterruptedException {
        Consumer<String, String> consumer = consumerFactory.getConsumer("test", "test-g1", false);
        Executors.newSingleThreadExecutor().execute(() -> {
            System.out.println("begin consume...");
            TopicPartition tp = new TopicPartition("test", 0);
            Set<TopicPartition> tps = Collections.singleton(tp);
            consumer.assign(tps);
            consumer.seekToBeginning(tps);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                records.forEach(r -> {
                    System.out.println(r.key());
                    System.out.println(r.offset());
                    System.out.println(r.value());
                });

            }
        });

        Producer<String, String> producer = producerFactory.getProducer("test");
        producer.send(new ProducerRecord<>("test", System.currentTimeMillis() + "", "hello-" + System.currentTimeMillis()));
        producer.flush();
        TimeUnit.SECONDS.sleep(2);

        producer.close();
    }

    public static void main(String[] args) {

    }

    @Test
    public void f() {
        int size = 300_0000;
        String prefix = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>(size);

        for (int i = 0; i < size; i++) {
            map.put(prefix + i, System.currentTimeMillis() + "");
        }

        long start = System.currentTimeMillis();
        long queryTimes = 0;
        long hit = 0;
        long unhit = 0;
        for (int i = 0; i < size * 1.3; i += 427) {
            queryTimes++;
            if (map.containsKey(prefix + i)) {
                hit++;
            } else {
                unhit++;
            }
        }
        long end = System.currentTimeMillis();

        System.out.println(queryTimes);
        System.out.println(hit);
        System.out.println(unhit);
        assert queryTimes == hit + unhit;
        System.out.println("每秒查询次数：" + queryTimes / (end - start) * 1000);
    }


}
