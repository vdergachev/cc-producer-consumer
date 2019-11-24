package com.cube.procon.producer.service;

import com.cube.procon.BaseTest;
import com.cube.procon.api.UserInputDto;
import com.cube.procon.model.UserInput;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.ACCEPTED;

class ProducerTest extends BaseTest {

    @Test
    void producerAcceptsAndProcessUserInput() {
        // given
        final var expectedKey = application;
        final var expectedMessage = new UserInput(application, FIRST_NAME, LAST_NAME);

        // when
        final var response = restTemplate.exchange(
                format("http://localhost:%d/user/input", port),
                HttpMethod.POST,
                httpEntity(new UserInputDto(FIRST_NAME, LAST_NAME)),
                String.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(ACCEPTED);
        assertThatKeyAndMessageAppearedInKafka(expectedKey, expectedMessage);
    }

    private static <T> HttpEntity<T> httpEntity(final T body) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}
