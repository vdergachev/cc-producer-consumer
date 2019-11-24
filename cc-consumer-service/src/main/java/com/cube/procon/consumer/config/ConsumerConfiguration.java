package com.cube.procon.consumer.config;

import com.cube.procon.consumer.redis.UserInputTemplate;
import com.cube.procon.property.YamlPropertySourceFactory;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisServer;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@PropertySource(factory = YamlPropertySourceFactory.class, value = "file:${consumer.config.file}")
public class ConsumerConfiguration {

    // To allow start consumer even if producer did's start yet and topic doesn't exist
    @Bean
    public NewTopic userInputTopic(@Value("${application.topic}") final String topic) {
        return new NewTopic(topic, 1, (short) 1);
    }

    @Bean
    public RedisServer redisServer(@Value("${spring.redis.host:localhost}") final String host,
                                   @Value("${spring.redis.port:6379}") final String port) {
        return new RedisServer(host, Integer.parseInt(port));
    }

    @Bean
    public JedisConnectionFactory connectionFactory(final RedisServer redisServer) {
        return new JedisConnectionFactory(new RedisStandaloneConfiguration(redisServer.getHost(), redisServer.getPort()));
    }

    @Bean
    public UserInputTemplate redisTemplate(final JedisConnectionFactory connectionFactory) {
        return new UserInputTemplate(connectionFactory);
    }
}
