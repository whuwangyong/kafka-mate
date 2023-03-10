package cn.whu.wy.kafkamate.service.client;

import cn.whu.wy.kafkamate.KafkaMateProperties;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author WangYong
 * Date 2023/03/10
 * Time 10:15
 */
public class BaseFactory {

    protected static final String DEFAULT_SERIALIZE_TOPICS = "_all_";
    protected static final String TOPICS_SEPARATOR = ",";

    protected final KafkaMateProperties kafkaMateProperties;


    public BaseFactory(KafkaMateProperties kafkaMateProperties) {
        this.kafkaMateProperties = kafkaMateProperties;
    }

    /**
     * 解析（反）序列化与topic的配置
     */
    protected void parseSerializerTopicConfig(Map<String, String> serializerTopicMap, SerializerType type) {
        List<KafkaMateProperties.Serializer> serializers = null;
        switch (type) {
            case KEY_SERIALIZER:
                serializers = kafkaMateProperties.getKeySerializers();
                break;
            case VALUE_SERIALIZER:
                serializers = kafkaMateProperties.getValueSerializers();
                break;
            case KEY_DESERIALIZER:
                serializers = kafkaMateProperties.getKeyDeserializers();
                break;
            case VALUE_DESERIALIZER:
                serializers = kafkaMateProperties.getValueDeserializers();
                break;
        }

        if (serializers == null || serializers.size() == 0) {
            throw new RuntimeException("请至少配置一个默认的" + type.name());
        }

        // 如果只配置了一个序列化类，则是所有topic通用的
        if (serializers.size() == 1) {
            KafkaMateProperties.Serializer serializer = serializers.get(0);
            if (StringUtils.equals(serializer.getTopics(), DEFAULT_SERIALIZE_TOPICS)) {
                serializerTopicMap.put(serializer.getTopics(), serializer.getClassName());
            } else {
                throw new RuntimeException(String.format("默认%s的topics应配置为%s", type.name(), DEFAULT_SERIALIZE_TOPICS));
            }
        } else {
            serializers.forEach(o -> {
                for (String topic : o.getTopics().split(TOPICS_SEPARATOR)) {
                    serializerTopicMap.put(topic.trim(), o.getClassName());
                }
            });
        }
    }

    protected String getSerializer(String topic, Map<String, String> serializerTopicMap) {
        String serializer = serializerTopicMap.get(topic);
        return serializer == null ? serializerTopicMap.get(DEFAULT_SERIALIZE_TOPICS) : serializer;
    }

    enum SerializerType {
        KEY_SERIALIZER,
        VALUE_SERIALIZER,
        KEY_DESERIALIZER,
        VALUE_DESERIALIZER
    }
}
