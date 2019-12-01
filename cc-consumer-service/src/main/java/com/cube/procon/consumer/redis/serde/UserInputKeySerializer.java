package com.cube.procon.consumer.redis.serde;

import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

public class UserInputKeySerializer extends StringRedisSerializer {

    private final static String PREFIX = "UserInput:";

    @Override
    public byte[] serialize(final String application) throws SerializationException {
        return super.serialize(PREFIX + application);
    }

    @Override
    public String deserialize(final byte[] bytes) throws SerializationException {
        final var key = super.deserialize(bytes);
        if (key == null) {
            return null;
        }
        return key.replaceFirst(PREFIX, "");
    }
}