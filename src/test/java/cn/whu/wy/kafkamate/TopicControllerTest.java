package cn.whu.wy.kafkamate;

import cn.whu.wy.kafkamate.bean.TopicInfo;
import cn.whu.wy.kafkamate.dto.response.ResponseDto;
import cn.whu.wy.kafkamate.restapi.RequestPath;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class TopicControllerTest {

    private static final String SERVER = "http://127.0.0.1:8080";

    @Test
    public void test() {
        RestTemplate restTemplate = new RestTemplate();

        Set<String> topics = new HashSet<>(100);
        for (int i = 1; i <= 100; i++) {
            topics.add("test-" + i);
        }

        ResponseEntity<ResponseDto> response1 = restTemplate.exchange(SERVER + RequestPath.TOPIC, HttpMethod.DELETE, new HttpEntity<>(topics), ResponseDto.class);
        log.info("delete topics response: {}", response1);

        Set<TopicInfo> topicInfos = topics.stream().map(t -> new TopicInfo(t, 1, (short) 1))
                .collect(Collectors.toSet());
        ResponseEntity<ResponseDto> response2 = restTemplate.postForEntity(SERVER + RequestPath.TOPIC, topicInfos, ResponseDto.class);
        log.info("create topics response: {}", response2);
    }
}
