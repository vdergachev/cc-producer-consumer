package com.cube.procon.consumer.service;

import com.cube.procon.consumer.config.ConsumerConfiguration;
import com.cube.procon.consumer.config.TestRedisConfiguration;
import com.cube.procon.consumer.redis.UserInputTemplate;
import com.cube.procon.model.UserInput;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(SpringExtension.class)
@Import(value = {TestRedisConfiguration.class, ConsumerConfiguration.class})
@EmbeddedKafka(partitions = 1, topics = "test-user-input")
@TestPropertySource(properties = {
        "consumer.config.file=config/consumer-test.yml",
        "spring.main.banner-mode=off"
})
@SpringBootTest
class ListenerIntegrationTest {

    private final static String FIRST_NAME = "Elon";
    private final static String LAST_NAME = "Musk";

    @Value("${application.name}")
    protected String application;

    @Value("${application.topic}")
    protected String topic;

    @Autowired
    private EmbeddedKafkaBroker kafkaBroker;

    @Autowired
    private UserInputTemplate inputTemplate;

    @Test
    void listenerSendsMessageToRedis() {
        // given
        final var message = new UserInput(application, FIRST_NAME, LAST_NAME);

        // when
        sendMessageToKafka(message);

        // then
        assertThatOneMessageAppearedInRedis(message);
    }

    private void sendMessageToKafka(final UserInput message) {

        final Map<String, Object> configs = Map.copyOf(
                KafkaTestUtils.producerProps(kafkaBroker)
        );

        final var keySerializer = new StringSerializer();
        final var messageSerializer = new JsonSerializer<UserInput>();

        final var producer = new KafkaProducer<>(configs, keySerializer, messageSerializer);

        producer.send(new ProducerRecord<>(topic, application, message));
        producer.flush();

    }

    private void assertThatOneMessageAppearedInRedis(final UserInput message) {
        var actualMessage = inputTemplate.opsForList().leftPop(application, 5, TimeUnit.SECONDS);
        assertEquals(message, actualMessage);

        var noMore = inputTemplate.opsForList().leftPop(application, 5, TimeUnit.SECONDS);
        assertNull(noMore);
    }

}
