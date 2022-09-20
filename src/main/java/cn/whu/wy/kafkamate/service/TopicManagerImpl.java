package cn.whu.wy.kafkamate.service;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicCollection;
import org.apache.kafka.common.Uuid;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
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
    public Set<String> listTopicsName(boolean listInternal) throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(listInternal));
        return listTopicsResult.names().get();
    }

    @Override
    public Collection<TopicListing> listTopics(boolean listInternal) throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(listInternal));
        return listTopicsResult.listings().get();
    }


    /**
     * 批量创建topics。过滤掉名字为空和已存在的topics
     */
    @Override
    public Object createTopics(Set<TopicInfo> topicInfos) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        Set<String> existTopics = listTopicsName(false);
        List<NewTopic> topicsToCreate = topicInfos.stream()
                .filter(topicInfo -> !StringUtils.isBlank(topicInfo.getName())
                        && topicInfo.getNumPartitions() != 0
                        && topicInfo.getReplicationFactor() != 0)
                .filter(topicInfo -> !existTopics.contains(topicInfo.getName()))
                .map(topicInfo -> new NewTopic(topicInfo.getName(), topicInfo.getNumPartitions(), topicInfo.getReplicationFactor()))
                .collect(Collectors.toList());
        adminClient.createTopics(topicsToCreate).all().get();
        long now = System.currentTimeMillis();
        String info = String.format("create topics: input size=%d, actually created size=%d, use %d ms.",
                topicInfos.size(), topicsToCreate.size(), now - start);
        log.info(info);
        return info;
    }

    /**
     * 批量删除topics。过滤掉不存在的topics
     */
    @Override
    public Object deleteTopics(Set<String> topics) throws ExecutionException, InterruptedException {
        Object o = doDeleteTopics(topics);
//        createFooTopic4TriggeringDelete();
        return o;
    }

    private Object doDeleteTopics(Set<String> topics) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        Collection<TopicListing> existTopics = listTopics(false);
        List<Uuid> topicsToDelete = existTopics.stream()
                .filter(t -> topics.contains(t.name()))
                .map(TopicListing::topicId)
                .collect(Collectors.toList());
        adminClient.deleteTopics(TopicCollection.ofTopicIds(topicsToDelete)).all().get();
        long now = System.currentTimeMillis();
        String info = String.format("delete topics: input size=%d, actually deleted size=%d, use %d ms.",
                topics.size(), topicsToDelete.size(), now - start);
        log.info(info);
        return info;
    }

    /**
     * 由于kafka删除topic不会立即生效，只是标记为删除。
     * 该方法创建一个临时topic，然后将其删除，试图快速触发kafka的删除机制
     */
    private void createFooTopic4TriggeringDelete() throws ExecutionException, InterruptedException {
        NewTopic foo = new NewTopic("foo_" + System.currentTimeMillis(), 1, (short) 1);
        adminClient.createTopics(Collections.singleton(foo)).all().get();
        adminClient.deleteTopics(Collections.singleton(foo.name())).all().get();
    }
}
