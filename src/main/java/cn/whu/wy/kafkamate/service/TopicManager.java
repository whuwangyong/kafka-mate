package cn.whu.wy.kafkamate.service;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import org.apache.kafka.clients.admin.TopicListing;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface TopicManager extends AdminClientAware {

    Set<String> listTopicsName(boolean listInternal) throws ExecutionException, InterruptedException;

    Collection<TopicListing> listTopics(boolean listInternal) throws ExecutionException, InterruptedException;

    Object createTopics(Set<TopicInfo> topicInfos) throws ExecutionException, InterruptedException;

    Object deleteTopics(Set<String> topics) throws ExecutionException, InterruptedException;


}
