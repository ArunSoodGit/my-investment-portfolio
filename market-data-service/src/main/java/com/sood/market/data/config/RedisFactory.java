package com.sood.market.data.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

@Factory
public class RedisFactory {

    @Singleton
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    @Singleton
    public StatefulRedisConnection<String, String> redisConnection(final RedisClient client) {
        return client.connect();
    }

    @Singleton
    @Named("mainRedis")
    public RedisCommands<String, String> redisCommands(final StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
}