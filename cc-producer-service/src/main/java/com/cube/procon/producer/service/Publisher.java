package com.cube.procon.producer.service;

import com.cube.procon.model.UserInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class Publisher {

    private final String topic;
    private final String application;
    private final KafkaTemplate<String, UserInput> kafkaTemplate;

    public Publisher(final KafkaTemplate<String, UserInput> kafkaTemplate,
            @Value("${application.name}") final String name,
            @Value("${application.topic}") final String topic) {

        this.application = name;
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(final String firstName, final String lastName) {

        final var input = new UserInput(application, firstName, lastName);
        log.info("Queue message {}", input);

        // Let's use application name as a key, potentially we create partition per application
        // in the topic and handle messages in individual wey (more flexibility in general)
        kafkaTemplate.send(topic, application, input);
    }
}
