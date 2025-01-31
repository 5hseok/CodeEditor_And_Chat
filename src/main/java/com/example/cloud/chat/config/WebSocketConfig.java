package com.example.cloud.chat.config;

import com.example.cloud.global.jwt.JwtTokenProvider;
import com.example.cloud.global.jwt.JwtValidationType;
import com.example.cloud.global.jwt.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    // 이 주소로 요청이 왔을 때 Socket 연결을 허용받을 수 있음 (CORS 설정)
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws/chat")
                .setAllowedOrigins("https://codablesite.netlify.app")
////                 SockJS를 사용할 수 있도록 설정
////                 SockJS는 클라이언트와 서버 간의 연결이 계속 설정되어있는 지를 지속적으로 확인할 수 있고
////                 연결이 끊어진 경우 다시 연결할 수 있도록 해줌.
////                 또한, SockJS는 WebSocket을 지원하지 않는 브라우저에서도 사용할 수 있도록 해줌.
////                 그러나, SockJS는 WebSocket보다 느리고, WebSocket의 모든 기능을 지원하지 않음.
////                 그리고 클라이언트와 서버가 지속적으로 heartbeat 신호를 주고 받기 때문에 매우 작은
////                 데이터이긴 하지만 트래픽이 많아진다면서버에 부하를 줄 수 있음.
//                 .withSockJS()
        ;
    }

    @Override
    // 메시지 브로커를 구성하는 메서드로
    // /subscribe로 시작하는 메시지를 구독하고 /publish로 시작하는 메시지를 발행
    public void configureMessageBroker(MessageBrokerRegistry registry){
        // 메모리 기반 메시지 브로커 활성화
        // 클라이언트에서 메시지를 수신(구독)할 Endpoint Prefix 설정
        registry.enableSimpleBroker("/subscribe");
        // 클라이언트에서 메시지를 송신할 Endpoint Prefix 설정
        registry.setApplicationDestinationPrefixes("/publish");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

                if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
                    String destination = accessor.getDestination();
                    String sessionId = accessor.getSessionId();
                    log.info("New subscription to channel: {} by session: {}", destination, sessionId);
                }

                return message;
            }
        });
    }

}
