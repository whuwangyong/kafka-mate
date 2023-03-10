package cn.whu.wy.kafkamate;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author WangYong
 * Date 2022/06/03
 */
@Configuration
public class BeanConfig {

    private final KafkaMateProperties kafkaMateProperties;

    @Autowired
    public BeanConfig(KafkaMateProperties kafkaMateProperties) {
        this.kafkaMateProperties = kafkaMateProperties;
    }

    @Bean
    public AdminClient adminClient() {
        Properties prop = new Properties();
        prop.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaMateProperties.getServers());
        return AdminClient.create(prop);
    }
}
