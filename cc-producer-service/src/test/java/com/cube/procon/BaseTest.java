package com.cube.procon;

import com.cube.procon.model.UserInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getSingleRecord;

@ExtendWith(SpringExtension.class)
@EmbeddedKafka(partitions = 1)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {
        "producer.config.file=config/producer-test.yml",
        "spring.main.banner-mode=off"
})
public abstract class BaseTest {

    protected final static String FIRST_NAME = "Steve";
    protected final static String LAST_NAME = "Jobs";
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final static int TIMEOUT = 5_000;

    @Value("${application.topic}")
    protected String topic;

    @Value("${application.name}")
    protected String application;

    @Value("${spring.kafka.bootstrap-servers}")
    protected String bootstrapServers;

    @Autowired
    protected TestRestTemplate restTemplate;

    @LocalServerPort
    protected int port;

    protected static String asString(final Object body) {
        try {
            return MAPPER.writeValueAsString(body);
        } catch (final JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected static Map<String, String> asMap(final String body) {
        try {
            return MAPPER.readValue(body, new MapTypeReference<>());
        } catch (final JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void assertThatKeyAndMessageAppearedInKafka(final String expectedKey, final UserInput expectedMessage) {

        final Map<String, Object> props = Map.of(
                BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                AUTO_OFFSET_RESET_CONFIG, "earliest",
                GROUP_ID_CONFIG, "test-group-id"
        );

        final var serializer = new StringDeserializer();
        final var deserializer = new JsonDeserializer<>(UserInput.class);

        try (final var consumer = new KafkaConsumer<>(props, serializer, deserializer)) {

            consumer.subscribe(List.of(topic));

            final var record = getSingleRecord(consumer, topic, TIMEOUT);

            assertNotNull(record);
            assertEquals(expectedKey, record.key());
            assertEquals(expectedMessage, record.value());
        }
    }

    private final static class MapTypeReference<K, V> extends TypeReference<Map<K, V>> {
    }
}
