package Invaders_SDP_server;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{

    // 실시간으로 전송된 위치 정보를 받아서 다른 클라이언트에게 전달하는 역할
    private final GameWebSocketHandler gameWebSocketHandler;

    // WebSocket 핸들러를 /game 엔드포인트에 연결하여
    // 클라이언트가 이 경로로 WebSocket 연결을 할 수 있게 함
    public WebSocketConfig(GameWebSocketHandler gameWebSocketHandler) {
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    // /game 경로로 클라이언트가 WebSocket 연결을 할 수 있게 함
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, "/game").setAllowedOrigins("*");
    }

}
