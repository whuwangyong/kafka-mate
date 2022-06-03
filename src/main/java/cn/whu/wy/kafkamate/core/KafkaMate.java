package cn.whu.wy.kafkamate.core;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author WangYong
 * Date 2022/03/13
 */
@Service
public class KafkaMate {

    private AdminClient adminClient;

    @Autowired

    public KafkaMate(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

}
