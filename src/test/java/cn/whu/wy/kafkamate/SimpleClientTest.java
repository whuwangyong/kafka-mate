package cn.whu.wy.kafkamate;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 * TODO consume方法不打印日志，为什么？因为consumer消费晚于producer生产，默认从latest消费，就消费不到前面的消息了
 */
@SpringBootTest
@Slf4j
public class SimpleClientTest {
    private static final String SERVER = "192.168.46.128:9092";

    @Test
    public void test() {
        startConsume();
        produce();
        sleep();
    }

    void startConsume() {
        Thread t = new Thread(this::consume);
        t.setName("T_TEST_CONSUMER");
        t.start();
    }

    void sleep() {
        try {
            TimeUnit.SECONDS.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    Properties consumerProperties() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", SERVER);
        props.setProperty("group.id", "test-g-" + System.currentTimeMillis());
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    Properties producerProperties() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", SERVER);
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    void consume() {
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties())) {
            consumer.subscribe(Collections.singleton("test"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records)
                    System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }

    void produce() {
        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties())) {
            for (int i = 1; i <= 5; i++) {
                ProducerRecord<String, String> record = new ProducerRecord<>("test", "hello-" + i);
                producer.send(record);
            }
            log.info("messages sent");
            producer.flush();
        }
    }

}
