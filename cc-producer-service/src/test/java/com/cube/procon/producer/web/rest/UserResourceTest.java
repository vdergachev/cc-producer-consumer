package com.cube.procon.producer.web.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cube.procon.BaseTest;
import com.cube.procon.api.UserInputDto;
import com.cube.procon.model.UserInput;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

@AutoConfigureMockMvc
class UserResourceTest extends BaseTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    protected KafkaTemplate<String, UserInput> kafkaTemplate;

    @Test
    void producerAcceptsUserInputRequestAndSendsItToKafka() throws Exception {

        // given
        final var expectedKey = application;
        final var expectedMessage = new UserInput(application, FIRST_NAME, LAST_NAME);

        final var webRequest = createWebRequest(new UserInputDto(FIRST_NAME, LAST_NAME));

        // when
        final var actualResult = mockMvc.perform(webRequest);

        // then
        final var actualResponse = actualResult.andExpect(status().isAccepted())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("", actualResponse);

        verify(kafkaTemplate).send(eq(topic), eq(expectedKey), eq(expectedMessage));
        verifyNoMoreInteractions(kafkaTemplate);
    }

    @Test
    void producerRejectsInvalidUserInputRequest() throws Exception {

        // given
        final var expectedErrors = Map.of(
                "firstName", "First name is mandatory",
                "lastName", "Last name is mandatory");

        final var webRequest = createWebRequest(new UserInputDto("", "")); // no name and surname in dto

        // when
        final var actualResult = mockMvc.perform(webRequest);

        // then
        final var actualResponse = actualResult.andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(expectedErrors, asMap(actualResponse));
        verifyNoInteractions(kafkaTemplate);
    }

    @Test
    void producerRejectsInvalidWebRequest() throws Exception {

        // given
        final var webRequest = createWebRequest("invalid"); // no name and surname in dto

        // when
        final var actualResult = mockMvc.perform(webRequest);

        // then
        final var actualResponse = actualResult.andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals("", actualResponse);
        verifyNoInteractions(kafkaTemplate);
    }

    private static MockHttpServletRequestBuilder createWebRequest(final UserInputDto dto) {
        return createWebRequest(asString(dto));
    }

    private static MockHttpServletRequestBuilder createWebRequest(final String content) {
        return MockMvcRequestBuilders
                .post("/user/input")
                .contentType(APPLICATION_JSON)
                .content(content)
                .accept(APPLICATION_JSON);
    }

}

