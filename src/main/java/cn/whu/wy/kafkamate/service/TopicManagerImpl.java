package cn.whu.wy.kafkamate.service;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author WangYong
 * Date 2022/03/13
 */
@Service
@Slf4j
public class TopicManagerImpl implements TopicManager {


    private final AdminClient adminClient;

    public TopicManagerImpl(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    @Override
    public AdminClient getAdminClient() {
        return adminClient;
    }

    @Override
    public Set<String> listTopics(boolean listInternal) throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(listInternal));
        return listTopicsResult.names().get();
    }

    /**
     * 批量创建topics。过滤掉名字为空和已存在的topics
     */
    @Override
    public Object createTopics(Set<TopicInfo> topicInfos) throws ExecutionException, InterruptedException {
        Set<String> existTopics = listTopics(false);
        List<NewTopic> topicsToCreate = topicInfos.stream()
                .filter(topicInfo -> !StringUtils.isBlank(topicInfo.getName())
                        && topicInfo.getNumPartitions() != 0
                        && topicInfo.getReplicationFactor() != 0)
                .filter(topicInfo -> !existTopics.contains(topicInfo.getName()))
                .map(topicInfo -> new NewTopic(topicInfo.getName(), topicInfo.getNumPartitions(), topicInfo.getReplicationFactor()))
                .collect(Collectors.toList());
        adminClient.createTopics(topicsToCreate).all().get();
        String info = String.format("input topics num: %d, created topics num: %d", topicInfos.size(), topicsToCreate.size());
        log.info(info);
        return info;
    }

    /**
     * 批量删除topics。过滤掉不存在的topics
     */
    @Override
    public Object deleteTopics(Set<String> topics) throws ExecutionException, InterruptedException {
        Set<String> existTopics = listTopics(false);
        Set<String> topicsToDelete = topics.stream()
                .filter(existTopics::contains)
                .collect(Collectors.toSet());
        adminClient.deleteTopics(topicsToDelete).all().get();
        String info = String.format("input topics num: %d, deleted topics num: %d", topics.size(), topicsToDelete.size());
        log.info(info);
        return info;
    }
}
