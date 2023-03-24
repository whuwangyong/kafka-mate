package cn.whu.wy.kafkamate.service.client;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import cn.whu.wy.kafkamate.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.TopicConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
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

    @Autowired
    TopicService topicService;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        final String topicName = "factory-test-topic";
        TopicInfo topicInfo = new TopicInfo(topicName, 1, (short) 1)
                .configs(Map.of(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT));
        topicService.createTopics(Set.of(topicInfo));
        Consumer<String, String> consumer = consumerFactory.getConsumer(topicName, IsolationLevel.READ_UNCOMMITTED);
        Executors.newSingleThreadExecutor().execute(() -> {
            log.info("begin consume...");
            TopicPartition tp = new TopicPartition(topicName, 0);
            Set<TopicPartition> tps = Collections.singleton(tp);
            consumer.assign(tps);
            consumer.seekToBeginning(tps);

            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                records.forEach(r -> {
                    log.info("offset={}, key={}, value={}", r.offset(), r.key(), r.value());
                });

            }
        });

        Producer<String, String> producer = producerFactory.getProducer(topicName);
        for (int i = 0; i < 5; i++) {
            producer.send(new ProducerRecord<>(topicName, "key-" + i, "value-" + LocalDateTime.now()));
        }

        producer.send(new ProducerRecord<>(topicName, "key-0", "value-updated"));

        producer.flush();
        TimeUnit.SECONDS.sleep(2000);

        producer.close();
    }

}
