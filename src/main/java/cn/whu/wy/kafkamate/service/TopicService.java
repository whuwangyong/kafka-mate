package cn.whu.wy.kafkamate.service;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import cn.whu.wy.kafkamate.service.client.ConsumerFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.IsolationLevel;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * @author WangYong
 * Date 2022/03/13
 */
@Service
@Slf4j
public class TopicService {


    private AdminClient adminClient;
    private ConsumerFactory consumerFactory;

    public TopicService(AdminClient adminClient, ConsumerFactory consumerFactory) {
        this.adminClient = adminClient;
        this.consumerFactory = consumerFactory;
    }


    public Set<String> listTopics(boolean listInternal) throws ExecutionException, InterruptedException {
        ListTopicsResult listTopicsResult = adminClient.listTopics(new ListTopicsOptions().listInternal(listInternal));
        return listTopicsResult.names().get();
    }

    public Map<String, TopicDescription> describeTopics(Collection<String> topicNames) throws ExecutionException, InterruptedException, TimeoutException {
        return adminClient.describeTopics(topicNames).allTopicNames().get(5000, TimeUnit.MICROSECONDS);
    }

    public TopicDescription describeTopic(String name) throws ExecutionException, InterruptedException, TimeoutException {
        return adminClient.describeTopics(Collections.singleton(name)).allTopicNames().
                get(1000, TimeUnit.MICROSECONDS).get(name);
    }


    /**
     * 列出topic的分区
     */
    public List<TopicPartition> topicPartitions(String topic) throws ExecutionException, InterruptedException, TimeoutException {
        TopicDescription topicDescription = describeTopic(topic);
        return topicDescription.partitions().stream()
                .map(o -> new TopicPartition(topic, o.partition()))
                .collect(Collectors.toList());

    }

    /**
     * 列出所有topic的分区
     * key=topic, value=该topic的分区
     */
    public Map<String, List<TopicPartition>> allTopicPartitions(boolean listInternal) throws ExecutionException, InterruptedException {
        Map<String, List<TopicPartition>> map = new HashMap<>();
        Set<String> topics = listTopics(listInternal);
        Map<String, TopicDescription> topicDescriptionMap =
                adminClient.describeTopics(topics).allTopicNames().get();
        topicDescriptionMap.forEach((topic, topicDescription) -> {
            List<TopicPartition> topicPartitions = topicDescription.partitions().stream()
                    .map(o -> new TopicPartition(topic, o.partition()))
                    .collect(Collectors.toList());
            map.put(topic, topicPartitions);
        });
        return map;
    }


    /**
     * 批量创建topics。过滤掉名字为空、分区数为0、副本数为0、已存在的topics
     */
    public String createTopics(Set<TopicInfo> topicInfos) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        Set<String> existTopics = listTopics(false);
        List<NewTopic> topicsToCreate = topicInfos.stream()
                .filter(topicInfo -> !StringUtils.isBlank(topicInfo.getName())
                        && topicInfo.getNumPartitions() != 0
                        && topicInfo.getReplicationFactor() != 0)
                .filter(topicInfo -> !existTopics.contains(topicInfo.getName()))
                .map(topicInfo -> new NewTopic(topicInfo.getName(), topicInfo.getNumPartitions(), topicInfo.getReplicationFactor()))
                .collect(Collectors.toList());
        adminClient.createTopics(topicsToCreate).all().get();
        long now = System.currentTimeMillis();
        String info = String.format("create topics: input %d, actually created %d, use %d ms.",
                topicInfos.size(), topicsToCreate.size(), now - start);
        log.info(info);
        return info;
    }

    /**
     * 批量删除topics。过滤掉不存在的topics
     */
    public String deleteTopics(Set<String> topics) throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        Set<String> existTopics = listTopics(false);
        List<String> topicsToDelete = topics.stream()
                .filter(existTopics::contains)
                .collect(Collectors.toList());
        adminClient.deleteTopics(topicsToDelete).all().get();
        long now = System.currentTimeMillis();
        String info = String.format("delete topics: input %d, actually deleted %d, use %d ms.",
                topics.size(), topicsToDelete.size(), now - start);
        log.info(info);
        return info;
    }


    public void deleteAllTopics() throws ExecutionException, InterruptedException {
        Set<String> allTopics = listTopics(true);
        adminClient.deleteTopics(allTopics).all().get();
    }

    public Map<TopicPartition, Long> getOffset(String topic, IsolationLevel isolationLevel) throws ExecutionException, InterruptedException, TimeoutException {
        Consumer<Object, Object> consumer = consumerFactory.getConsumer(topic, isolationLevel);
        try {
            List<TopicPartition> partitions = topicPartitions(topic);
            consumer.assign(partitions);
            return consumer.endOffsets(partitions);
        } catch (Exception e) {
            log.error("topic={}, getOffset error", topic, e);
            return Collections.emptyMap();
        }
    }
}
