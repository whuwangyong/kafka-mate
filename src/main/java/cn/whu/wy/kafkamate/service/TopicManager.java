package cn.whu.wy.kafkamate.service;

import cn.whu.wy.kafkamate.bean.TopicInfo;

import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface TopicManager extends AdminClientAware {

    Set<String> listTopics(boolean listInternal) throws ExecutionException, InterruptedException;

    Object createTopics(Set<TopicInfo> topicInfos) throws ExecutionException, InterruptedException;

    Object deleteTopics(Set<String> topics) throws ExecutionException, InterruptedException;


}
