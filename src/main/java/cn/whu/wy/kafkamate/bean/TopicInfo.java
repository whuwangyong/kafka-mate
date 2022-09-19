package cn.whu.wy.kafkamate.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopicInfo {
    String name;
    int numPartitions;
    short replicationFactor;


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
