package com.example.cloud.chat.config;

import com.example.cloud.chat.service.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfig {

    private static final String TOPIC_NAME = "topic";

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    // Redis 메시지 수신 및 리스너에 전달하는 컨테이너 설정 빈 등록
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
                                                                       MessageListenerAdapter listenerAdapter,
                                                                       ChannelTopic channelTopic) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(listenerAdapter, channelTopic);
        return container;
    }

    @Bean
    // RedisSubscriber 클래스를 사용해 메시지를 처리하는 리스너 설정
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        log.info("listenerAdapter");
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    // Redis 데이터를 직렬화하고 역직렬화하는 데 사용
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // Jackson ObjectMapper 설정 (LocalDateTime 지원 추가)
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())  // Java 8 날짜/시간 지원
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 타임스탬프 대신 ISO 형식 사용

        // 새로운 생성자 방식으로 ObjectMapper 적용
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

    @Bean
    // Redis 서버 연결 설정 및 관리 빈 등록
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    // 채널 토픽 설정 빈 등록
    public ChannelTopic channelTopic() {
        return new ChannelTopic(TOPIC_NAME);
    }
}
