package com.cube.procon.consumer.service;


import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cube.procon.consumer.redis.UserInputTemplate;
import com.cube.procon.model.UserInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;

@ExtendWith(MockitoExtension.class)
class ListenerTest {

    private final static String APPLICATION = "test-app";
    private final static String FIRST_NAME = "Elon";
    private final static String LAST_NAME = "Musk";

    @Mock
    private UserInputTemplate userInputTemplate;

    @InjectMocks
    private Listener listener;

    @Test
    void listenerSendsMessageToRedis() {
        // given
        final var listOperations = givenListOperations(userInputTemplate);

        final var message = new UserInput(APPLICATION, FIRST_NAME, LAST_NAME);

        // when
        listener.consume(message);

        // then
        verify(listOperations).leftPush(eq(APPLICATION), eq(message));
        verifyNoMoreInteractions(listOperations);
    }

    private ListOperations<String, UserInput> givenListOperations(final UserInputTemplate template) {
        final var fakeOps = mock(TestListOperations.class);
        given(template.opsForList()).willReturn(fakeOps);
        return fakeOps;
    }

    private interface TestListOperations extends ListOperations<String, UserInput> {
    }
}
