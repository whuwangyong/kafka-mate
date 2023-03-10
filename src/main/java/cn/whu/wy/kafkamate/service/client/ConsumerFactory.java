package cn.whu.wy.kafkamate.service.client;

import cn.whu.wy.kafkamate.KafkaMateProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 核心方法是 getConsumer，按照不同的topic，获取不同序列化方式的consumer，
 * 使用方用完之后需手动close以释放资源。
 * 简单起见，每次使用都新建，暂不池化
 *
 * @author WangYong
 * Date 2023/03/09
 * Time 16:14
 */
@Service
public class ConsumerFactory extends BaseFactory {
    // key=topic, value=serialize class
    private final Map<String, String> keyDeserializerTopicMap = new HashMap<>();
    private final Map<String, String> valueDeserializerTopicMap = new HashMap<>();


    public ConsumerFactory(KafkaMateProperties kafkaMateProperties) {
        super(kafkaMateProperties);
        parseSerializerTopicConfig(keyDeserializerTopicMap, SerializerType.KEY_DESERIALIZER);
        parseSerializerTopicConfig(valueDeserializerTopicMap, SerializerType.VALUE_DESERIALIZER);
    }

    public <K, V> Consumer<K, V> getConsumer(String topic, Properties props) {
        props.setProperty("bootstrap.servers", kafkaMateProperties.getServers());
        props.setProperty("key.deserializer", getSerializer(topic, keyDeserializerTopicMap));
        props.setProperty("value.deserializer", getSerializer(topic, valueDeserializerTopicMap));
        return new KafkaConsumer<>(props);
    }

    public <K, V> Consumer<K, V> getConsumer(String topic, String groupId, boolean readCommitted) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaMateProperties.getServers());
        props.setProperty("key.deserializer", getSerializer(topic, keyDeserializerTopicMap));
        props.setProperty("value.deserializer", getSerializer(topic, valueDeserializerTopicMap));
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        if (readCommitted) {
            props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        } else {
            props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_uncommitted");
        }
        return new KafkaConsumer<>(props);
    }


}
