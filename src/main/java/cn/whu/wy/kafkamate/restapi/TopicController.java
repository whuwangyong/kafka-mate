package cn.whu.wy.kafkamate.restapi;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import cn.whu.wy.kafkamate.service.TopicService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(RequestPath.TOPIC)
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @PostMapping
    public ResponseDto createTopics(@RequestBody Set<TopicInfo> topicInfos) {
        try {
            Object result = topicService.createTopics(topicInfos);
            return ResponseDto.success(result);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseDto.fail(e.getMessage(), "");
        }
    }

    @DeleteMapping
    public ResponseDto deleteTopics(@RequestBody Set<String> topics) {
        try {
            topicService.deleteTopics(topics);
            return ResponseDto.success("");
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return ResponseDto.fail(e.getMessage(), "");
        }
    }
}
