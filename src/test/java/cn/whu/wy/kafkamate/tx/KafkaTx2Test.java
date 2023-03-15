package cn.whu.wy.kafkamate.tx;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import cn.whu.wy.kafkamate.service.TopicService;
import cn.whu.wy.kafkamate.service.client.ConsumerFactory;
import cn.whu.wy.kafkamate.service.client.ProducerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 测试生产者在事务未提交时直接退出，在事务超时之前，另一生产者使用相同的事务id，能继续发送消息吗？
 * 测试结果：能。而且消费者立即就消费到了，并不会等待前面未提交的事务超时。
 * 但是，如果接替的生产者与之前退出的生产者不是相同id，则会等待事务超时。——Kafka这样的设计是符合常理的。
 *
 * @author WangYong
 * Date 2023/03/13
 * Time 13:49
 */
@SpringBootTest
@Slf4j
public class KafkaTx2Test {

    @Autowired
    private TopicService topicService;
    @Autowired
    private ProducerFactory producerFactory;
    @Autowired
    private ConsumerFactory consumerFactory;

    private final String TOPIC = "tx-topic";
    private final String TX_ID = "tx-id-1";

    @Test
    public void f() throws ExecutionException, InterruptedException {
        topicService.createTopics(Collections.singleton(new TopicInfo(TOPIC, 1, (short) 1)));

        Executors.newSingleThreadExecutor().execute(() -> {
            Consumer<String, String> consumer = consumerFactory.getConsumer(TOPIC, IsolationLevel.READ_COMMITTED);
            TopicPartition tp = new TopicPartition(TOPIC, 0);
            consumer.assign(Collections.singleton(tp));
            consumer.seekToBeginning(Collections.singleton(tp));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(r -> {
                    log.info("offset={}, key={}, value={}", r.offset(), r.key(), r.value());
                });
            }
        });


        TimeUnit.SECONDS.sleep(1);
        Thread p1 = new Thread(() -> {
            Producer<String, String> producer1 = producerFactory.getProducer(TOPIC, TX_ID, 1000 * 60 * 5);
            producer1.initTransactions();
            producer1.beginTransaction();
            log.info("send 1");
            producer1.send(new ProducerRecord<>(TOPIC, LocalDateTime.now() + ":hello-1"));
            producer1.commitTransaction();

            producer1.beginTransaction();
            for (int i = 0; i < 1000; i++) {
                producer1.send(new ProducerRecord<>(TOPIC, LocalDateTime.now() + ":hello-2"));
                // 只发送，不提交
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (i % 100 == 0) {
                    log.info("send 2-" + i);
                    producer1.flush();
                }
            }
        });
        p1.start();
        TimeUnit.SECONDS.sleep(4);

        // 模拟producer1宕机
//        p1.stop();

        // p2使用与p1相同的事务id，能否立即发送消息？消费者能否立即消费到？
        Producer<String, String> producer2 = producerFactory.getProducer(TOPIC, TX_ID, 1000 * 60 * 5);
        log.info("init-1");
        producer2.initTransactions();
        log.info("init-2");
        producer2.beginTransaction();
        log.info("send 3");
        producer2.send(new ProducerRecord<>(TOPIC, LocalDateTime.now() + ":hello-3"));
        producer2.commitTransaction();

        TimeUnit.SECONDS.sleep(4);
        producer2.beginTransaction();
        log.info("send 4");
        producer2.send(new ProducerRecord<>(TOPIC, LocalDateTime.now() + ":hello-4"));
        producer2.commitTransaction();

        TimeUnit.SECONDS.sleep(100);
    }

}
