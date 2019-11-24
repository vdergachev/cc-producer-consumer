package com.cube.procon.consumer.redis;

import com.cube.procon.consumer.redis.serde.UserInputKeySerializer;
import com.cube.procon.consumer.redis.serde.UserInputValueSerializer;
import com.cube.procon.model.UserInput;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

public class UserInputTemplate extends RedisTemplate<String, UserInput> {

    public UserInputTemplate(final RedisConnectionFactory connectionFactory) {
        setConnectionFactory(connectionFactory);

        setKeySerializer(new UserInputKeySerializer());
        setValueSerializer(new UserInputValueSerializer());

        afterPropertiesSet();
    }
}
