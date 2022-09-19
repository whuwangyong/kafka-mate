package cn.whu.wy.kafkamate.service;

import org.apache.kafka.clients.admin.AdminClient;

public interface AdminClientAware {

    AdminClient getAdminClient();
}
