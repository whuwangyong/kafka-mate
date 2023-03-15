package cn.whu.wy.kafkamate.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.stereotype.Service;

/**
 * @author WangYong
 * Date 2023/03/13
 * Time 09:19
 */
@Service
public class ClusterService {

    private final AdminClient adminClient;

    public ClusterService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public Object metrics() {
        return adminClient.metrics();
    }
}
