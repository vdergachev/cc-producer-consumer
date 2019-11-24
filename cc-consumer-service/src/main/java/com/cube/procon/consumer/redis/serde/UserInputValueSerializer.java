package com.cube.procon.consumer.redis.serde;

import com.cube.procon.model.UserInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.io.IOException;

public class UserInputValueSerializer implements RedisSerializer<UserInput> {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public byte[] serialize(final UserInput input) {
        try {
            return MAPPER.writeValueAsBytes(input);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public UserInput deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            return MAPPER.readValue(bytes, UserInput.class);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}