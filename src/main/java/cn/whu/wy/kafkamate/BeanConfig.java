package cn.whu.wy.kafkamate;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * @author WangYong
 * Date 2022/06/03
 */
@Configuration
public class BeanConfig {

    @Value("${cn.whu.wy.kafkamate.servers}")
    private String bootStrapServers;

    @Bean
    public AdminClient adminClient() {
        Properties prop = new Properties();
        prop.setProperty(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        return AdminClient.create(prop);
    }
}
