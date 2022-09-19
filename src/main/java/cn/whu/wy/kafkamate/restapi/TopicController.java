package cn.whu.wy.kafkamate.restapi;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import cn.whu.wy.kafkamate.dto.response.ResponseDto;
import cn.whu.wy.kafkamate.service.TopicManager;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(RequestPath.TOPIC)
public class TopicController {

    private final TopicManager topicManager;

    public TopicController(TopicManager topicManager) {
        this.topicManager = topicManager;
    }

    @PostMapping
    public ResponseDto createTopics(@RequestBody Set<TopicInfo> topicInfos) {
        try {
            Object result = topicManager.createTopics(topicInfos);
            return ResponseDto.success(result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseDto.fail(e.getMessage(), "");
        }
    }

    @DeleteMapping
    public ResponseDto deleteTopics(@RequestBody Set<String> topics) {
        try {
            topicManager.deleteTopics(topics);
            return ResponseDto.success("");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseDto.fail(e.getMessage(), "");
        }
    }
}
