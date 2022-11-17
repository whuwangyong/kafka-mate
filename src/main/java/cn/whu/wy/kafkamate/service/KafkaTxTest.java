package cn.whu.wy.kafkamate.service;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.AuthorizationException;
import org.apache.kafka.common.errors.OutOfOrderSequenceException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * kafka事务测试。测试未关闭的事务，对消费者的影响
 *
 * @author WangYong
 * Date 2022/11/16
 * Time 14:05
 */
@Service
@Slf4j
public class KafkaTxTest implements InitializingBean {

    private final String bootstrapServers = "192.168.136.128:9092";

    @Autowired
    private TopicManager topicManager;

    @Override
    public void afterPropertiesSet() throws Exception {
//        test();
    }

    void test() throws Exception {
        final String testTopic = "test-tx";

        TopicInfo topicInfo = new TopicInfo(testTopic, 1, (short) 1);
        topicManager.createTopics(Set.of(topicInfo));

        Executors.newSingleThreadExecutor().execute(() -> {
            try (Consumer<String, String> consumer = genRcConsumer("rc-consumer")) {
                consumer.subscribe(Collections.singleton(testTopic));
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    records.forEach(r -> log.info("consumer[rc] received: {}", r.value()));
                    log.info("rc pool...");
                }
            }
        });

        Executors.newSingleThreadExecutor().execute(() -> {
            try (Consumer<String, String> consumer = genRuncConsumer("runc-consumer")) {
                consumer.subscribe(Collections.singleton("test-tx"));
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                    records.forEach(r -> log.info("consumer[read_uncommitted] received: {}", r.value()));
                    log.info("runc pool...");
                }
            }
        });


        // 每秒发1个消息，10条消息
        Executors.newSingleThreadExecutor().execute(() -> {
            try (Producer<String, String> producer = genProducer()) {
                for (int i = 0; i < 10; i++) {
                    producer.send(new ProducerRecord<>(testTopic, "msg-" + i));
                    TimeUnit.SECONDS.sleep(1);
                    log.info("[nontx-producer]sent msg-" + i);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // 每秒一个事务，每个事务发2条消息，10个事务，20条消息
        Executors.newSingleThreadExecutor().execute(() -> {
            Producer<String, String> producer = genTxProducer("tx-producer-1");
            producer.initTransactions();

            for (int i = 2000; i < 2020; i += 2) {
                try {
                    producer.beginTransaction();

                    producer.send(new ProducerRecord<>(testTopic, "msg-" + i));
                    log.info("[tx-producer-1]sent tx msg-" + i);
                    producer.send(new ProducerRecord<>(testTopic, "msg-" + (i + 1)));
                    log.info("[tx-producer-1]sent tx msg-" + (i + 1));

                    TimeUnit.SECONDS.sleep(1);
                    producer.commitTransaction();
                } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                    // We can't recover from these exceptions, so our only option is to close the producer and exit.
                    producer.close();
                    log.error("tx-producer-1 closed", e);
                } catch (KafkaException e) {
                    // For all other exceptions, just abort the transaction and try again.
                    producer.abortTransaction();
                    log.warn("tx-producer-1 abort tx");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // 每秒1个事务，每个事务发2条消息
        // 在2个事务后sleep。即，commit了4条消息，未commit的有2条
        Executors.newSingleThreadExecutor().execute(() -> {
            Producer<String, String> producer = genTxProducer("tx-producer-2");
            producer.initTransactions();

            for (int i = 1000; i < 2000; i += 2) {
                try {
                    producer.beginTransaction();
                    producer.send(new ProducerRecord<>(testTopic, "msg-" + i));
                    log.info("[tx-producer-2]sent tx msg-" + i);

                    producer.send(new ProducerRecord<>(testTopic, "msg-" + (i + 1)));
                    log.info("[tx-producer-2]sent tx msg-" + (i + 1));

                    TimeUnit.SECONDS.sleep(1);


                    // 挂起事务
                    if (i == 1004) {
//                        log.warn("tx-producer-2 hanging tx");
                        TimeUnit.SECONDS.sleep(5);
//                        log.warn("tx-producer-2 system exit");
//                        System.exit(-1);
//                        TimeUnit.MINUTES.sleep(60);
                        log.info("tx-producer-1 stop");
                        Thread.currentThread().stop();
                    }

                    producer.commitTransaction();
                } catch (ProducerFencedException | OutOfOrderSequenceException | AuthorizationException e) {
                    // We can't recover from these exceptions, so our only option is to close the producer and exit.
                    producer.close();
                    log.error("tx-producer-2 closed", e);
                } catch (KafkaException e) {
                    // For all other exceptions, just abort the transaction and try again.
                    producer.abortTransaction();
                    log.warn("tx-producer-2 abort tx");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("thread tx-producer-2 exit");
        });


        // 10 秒后，使用挂起线程的事务id，创建一个producer，强制结束之前挂起的事务
//        Executors.newSingleThreadExecutor().execute(() -> {
//            try {
//                TimeUnit.SECONDS.sleep(10);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            Producer<String, String> producer = genTxProducer("tx-producer-1");
//            log.info("tx-producer-1 init again");
//            producer.initTransactions();
//        });

    }


    Producer<String, String> genTxProducer(String txId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("transactional.id", txId);
//        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10000); // 10 min
        return new KafkaProducer<>(props);
    }

    Producer<String, String> genProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("linger.ms", 1);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }

    Consumer<String, String> genRuncConsumer(String gid) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("group.id", gid);
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return new KafkaConsumer<>(props);
    }

    Consumer<String, String> genRcConsumer(String gid) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", bootstrapServers);
        props.setProperty("group.id", gid);
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "1000");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        return new KafkaConsumer<>(props);
    }


}
