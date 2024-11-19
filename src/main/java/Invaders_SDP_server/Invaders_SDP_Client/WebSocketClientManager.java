package Invaders_SDP_server.Invaders_SDP_Client;

import Invaders_SDP_server.GameStateDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.logging.Logger;

public class WebSocketClientManager {
    private static final Logger logger = Logger.getLogger(WebSocketClientManager.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final URI serverUri; // serverUri를 URI 타입으로 변경
    private final GameStateManager gameStateManager;
    private WebSocketSession session;

    public WebSocketClientManager(String serverUri, GameStateManager gameStateManager) {
        this.serverUri = URI.create(serverUri); // 문자열을 URI로 변환
        this.gameStateManager = gameStateManager;
        connectToServer();
    }

    private void connectToServer() {
        StandardWebSocketClient client = new StandardWebSocketClient();
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders(); // WebSocketHttpHeaders 추가

        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                try {
                    // 서버에서 받은 메시지를 GameStateDTO로 변환
                    GameStateDTO gameState = objectMapper.readValue(message.getPayload(), GameStateDTO.class);
                    gameStateManager.updateGameState(gameState);
                } catch (Exception e) {
                    logger.severe("Failed to parse server message: " + e.getMessage());
                }
            }

            @Override
            public void afterConnectionEstablished(WebSocketSession session) {
                WebSocketClientManager.this.session = session; // 세션 저장
                logger.info("WebSocket 연결 성공!");
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                logger.severe("WebSocket transport error: " + exception.getMessage());
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus closeStatus) {
                logger.info("WebSocket 연결이 종료되었습니다. 사유: " + closeStatus.getReason());
            }
        };

        // WebSocket 연결 실행
        ListenableFuture<WebSocketSession> future = client.doHandshake(handler, headers, serverUri);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(WebSocketSession result) {
                logger.info("WebSocket 연결 성공!");
                session = result; // 세션 저장
            }

            @Override
            public void onFailure(Throwable ex) {
                logger.severe("WebSocket 연결 실패: " + ex.getMessage());
            }
        });
    }

    // 메시지를 서버로 전송
    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                logger.info("Sent message to server: " + message);
            } catch (Exception e) {
                logger.severe("Failed to send message: " + e.getMessage());
            }
        } else {
            logger.warning("WebSocket 연결이 열려 있지 않습니다.");
        }
    }
}
