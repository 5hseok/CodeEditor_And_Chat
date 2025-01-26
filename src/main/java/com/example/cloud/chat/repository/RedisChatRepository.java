package com.example.cloud.chat.repository;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories
public interface RedisChatRepository extends RedisHash {
}
