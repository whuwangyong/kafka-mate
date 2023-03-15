package cn.whu.wy.kafkamate.service.client;

import cn.whu.wy.kafkamate.KafkaMateProperties;
import cn.whu.wy.kafkamate.service.Utils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.IsolationLevel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Locale;
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

    public <K, V> Consumer<K, V> getConsumer(String topic, IsolationLevel isolationLevel) {
        Properties props = new Properties();
        props.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaMateProperties.getServers());
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, getSerializer(topic, keyDeserializerTopicMap));
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, getSerializer(topic, valueDeserializerTopicMap));
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "gid_" + Utils.getTimestamp());
        props.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, isolationLevel.toString().toLowerCase(Locale.ROOT));
        return new KafkaConsumer<>(props);
    }


    /**
     * 使用什么序列化类，以及是否消费未提交的消息，决定了该消费者的类型
     */
    static class ConsumerType {
        String serializer;
        IsolationLevel isolationLevel;

        public ConsumerType(String serializer, IsolationLevel isolationLevel) {
            this.serializer = serializer;
            this.isolationLevel = isolationLevel;
        }
    }


}
