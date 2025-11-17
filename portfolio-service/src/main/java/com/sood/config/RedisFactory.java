package com.sood.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.micronaut.context.annotation.Factory;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

/**
 * Factory for creating and configuring Redis client beans.
 * Provides singleton instances for Redis connection management.
 */
@Factory
public class RedisFactory {

    /**
     * Creates a Redis client instance.
     *
     * @return configured Redis client
     */
    @Singleton
    public RedisClient redisClient() {
        return RedisClient.create("redis://localhost:6379");
    }

    /**
     * Creates a stateful Redis connection.
     *
     * @param client the Redis client
     * @return stateful Redis connection
     */
    @Singleton
    public StatefulRedisConnection<String, String> redisConnection(final RedisClient client) {
        return client.connect();
    }

    /**
     * Creates synchronous Redis commands interface.
     *
     * @param connection the Redis connection
     * @return synchronous Redis commands
     */
    @Singleton
    @Named("mainRedis")
    public RedisCommands<String, String> redisCommands(final StatefulRedisConnection<String, String> connection) {
        return connection.sync();
    }
}