package cn.whu.wy.kafkamate.bean;

import lombok.Data;

import java.util.Map;
import java.util.Objects;

@Data
public class TopicInfo {
    String name;
    int numPartitions;
    short replicationFactor;
    Map<String, String> configs;


    public TopicInfo() {
    }

    public TopicInfo(String name, int numPartitions, short replicationFactor) {
        this.name = name;
        this.numPartitions = numPartitions;
        this.replicationFactor = replicationFactor;
    }

    public TopicInfo configs(Map<String, String> configs) {
        this.configs = configs;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TopicInfo topicInfo = (TopicInfo) o;
        return name.equals(topicInfo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
