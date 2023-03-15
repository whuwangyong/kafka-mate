package cn.whu.wy.kafkamate.service.client;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author WangYong
 * Date 2023/03/15
 * Time 20:34
 */
@Slf4j
public class ConsumerPool {

    // 装消费者的阻塞队列
    private BlockingQueue<KafkaConsumer> consumersQueue;

    // 池的名字，主要是为了在日志和异常中体现是哪个池出问题了
    private String poolName;


    public ConsumerPool(BlockingQueue<KafkaConsumer> consumersQueue, String poolName) {
        this.consumersQueue = consumersQueue;
        this.poolName = poolName;
    }

    // 从池里面取一个消费者
    @SneakyThrows
    public KafkaConsumer get() {
        KafkaConsumer consumer = consumersQueue.poll(1, TimeUnit.SECONDS);
        if (consumer == null) {
            log.warn("消费者池耗尽, poolName={},consumersQueue.size={},consumersQueue.remainingCapacity={}",
                    poolName, consumersQueue.size(), consumersQueue.remainingCapacity());
            throw new Exception("消费者池耗尽，等待1秒仍然获取不到consumer. poolName=" + poolName);
        }
        if (consumersQueue.size() < consumersQueue.remainingCapacity()) {
            log.warn("消费者池剩余量不到一半, poolName={},consumersQueue.size={},consumersQueue.remainingCapacity={}",
                    poolName, consumersQueue.size(), consumersQueue.remainingCapacity());
        }
        return consumer;
    }

    // 将消费者还回池里
    @SneakyThrows
    public void add(KafkaConsumer consumer) {
        consumersQueue.put(consumer);
    }
}
