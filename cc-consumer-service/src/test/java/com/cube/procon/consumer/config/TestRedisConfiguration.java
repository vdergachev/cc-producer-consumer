package com.cube.procon.consumer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisServer;

import javax.annotation.PreDestroy;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@Slf4j
@Configuration
public class TestRedisConfiguration {

    private redis.embedded.RedisServer embeddedRedis;

    @Bean
    public RedisServer testRedisServer() {

        final var port = findAvailableTcpPort();
        embeddedRedis = redis.embedded.RedisServer.builder()
                .port(port)
                .setting("bind 127.0.0.1")
                .setting("daemonize no")
                .setting("appendonly no")
                .setting("maxmemory 64M")
                .build();

        embeddedRedis.start();
        log.info("Redis server started");

        return new RedisServer("localhost", port);
    }

    @PreDestroy
    public void tearDown() {
        embeddedRedis.stop();
        log.info("Redis server stopped");
    }

}
