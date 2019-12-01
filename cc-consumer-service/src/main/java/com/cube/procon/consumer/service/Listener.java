package com.cube.procon.consumer.service;

import com.cube.procon.consumer.redis.UserInputTemplate;
import com.cube.procon.model.UserInput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class Listener {

    private final UserInputTemplate userInputTemplate;

    @KafkaListener(topics = "${application.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(@Payload final UserInput input) {
        log.info("Received message {}", input);
        userInputTemplate.opsForList().leftPush(input.getApplication(), input);
    }
}
