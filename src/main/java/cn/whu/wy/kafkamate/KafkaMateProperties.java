package cn.whu.wy.kafkamate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author WangYong
 * Date 2023/03/09
 * Time 15:00
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cn.whu.wy.kafkamate")
public class KafkaMateProperties {

    private String servers;

    private List<Serializer> keySerializers;
    private List<Serializer> valueSerializers;

    private List<Serializer> keyDeserializers;
    private List<Serializer> valueDeserializers;


    @Data
    public static class Serializer {
        private String className;
        private String topics;
    }


}
