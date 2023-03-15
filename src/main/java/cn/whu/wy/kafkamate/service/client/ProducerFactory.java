package cn.whu.wy.kafkamate.service.client;

import cn.whu.wy.kafkamate.KafkaMateProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 核心方法是 getProducer，按照不同的topic，获取不同序列化方式的producer，
 * 使用方用完之后需手动close以释放资源。
 * 简单起见，每次使用都新建，暂不池化
 *
 * @author WangYong
 * Date 2023/03/09
 * Time 16:14
 */
@Service
public class ProducerFactory extends BaseFactory {
    // key=topic, value=serialize class
    private final Map<String, String> keySerializerTopicMap = new HashMap<>();
    private final Map<String, String> valueSerializerTopicMap = new HashMap<>();


    public ProducerFactory(KafkaMateProperties kafkaMateProperties) {
        super(kafkaMateProperties);
        parseSerializerTopicConfig(keySerializerTopicMap, SerializerType.KEY_SERIALIZER);
        parseSerializerTopicConfig(valueSerializerTopicMap, SerializerType.VALUE_SERIALIZER);
    }

    public <K, V> Producer<K, V> getProducer(String topic) {
        Properties props = baseProperties(topic);
        return new KafkaProducer<>(props);
    }

    public <K, V> Producer<K, V> getProducer(String topic, String txId, int txTimeoutMs) {
        Properties props = baseProperties(topic);
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, txId);
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, txTimeoutMs);
        return new KafkaProducer<>(props);
    }

    public <K, V> Producer<K, V> getProducer(String topic, Properties props) {
        Properties baseProperties = baseProperties(topic);
        for (Object key : props.keySet()) {
            baseProperties.put(key, props.get(key));
        }
        return new KafkaProducer<>(props);
    }

    private Properties baseProperties(String topic) {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaMateProperties.getServers());
        props.put("linger.ms", 1);
        props.put("key.serializer", getSerializer(topic, keySerializerTopicMap));
        props.put("value.serializer", getSerializer(topic, valueSerializerTopicMap));
        return props;
    }


}
