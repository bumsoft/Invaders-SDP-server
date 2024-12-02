package Invaders_SDP_server;

import Invaders_SDP_server.WebSocket.GameWebSocketHandler;
import Invaders_SDP_server.data.Player;
import Invaders_SDP_server.entity.Room;
import Invaders_SDP_server.repository.RoomRepository;
import Invaders_SDP_server.service.GameService;
import Invaders_SDP_server.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.CloseStatus;

import java.util.Map;

import static org.mockito.Mockito.*;

public class GameWebSocketHandlerTest {

    @InjectMocks
    private GameWebSocketHandler gameWebSocketHandler;

    @Mock
    private WebSocketSession session;

    @Mock
    private GameService gameService;

    @Mock
    private RoomService roomService;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mockito 초기화
        gameWebSocketHandler = new GameWebSocketHandler(gameService); // Handler 초기화
    }

    // WebSocket 연결 후 Player가 세션에 추가되는지 확인
    @Test
    void testAfterConnectionEstablished() {
        gameWebSocketHandler.afterConnectionEstablished(session);
        verify(session).getAttributes(); // 세션의 속성 확인
    }

    // "create-username" 메시지 처리 테스트
    @Test
    void testCreateRoom() throws Exception {
        String messageContent = "create-username";
        TextMessage message = new TextMessage(messageContent);

        when(roomService.createRoom("username")).thenReturn(new Room()); // 방 생성 Mocking

        gameWebSocketHandler.handleTextMessage(session, message);

        verify(session).sendMessage(new TextMessage("Created-" + anyString())); // "Created-" 메시지 확인
    }

    // "join-username-roomId" 메시지 처리 테스트
    @Test
    void testJoinRoom() throws Exception {
        String messageContent = "join-username-1";
        TextMessage message = new TextMessage(messageContent);

        Room room = new Room();
        room.setId(1L);
        when(roomService.joinRoom("username", 1L)).thenReturn(room); // 방 조인 Mocking

        gameWebSocketHandler.handleTextMessage(session, message);

        verify(session).sendMessage(argThat(textMessage -> textMessage.getPayload().equals("Joined-username-username")));
        //verify(session).sendMessage(new TextMessage("Joined-username-username")); // "Joined-" 메시지 확인
    }

    // "ready-username" 메시지 처리 테스트
    @Test
    void testReadyRoom() throws Exception {
        String messageContent = "ready-username";
        TextMessage message = new TextMessage(messageContent);

        Room room = mock(Room.class);
        when(roomService.playerReady("username")).thenReturn(room); // 플레이어 준비 Mocking
        when(room.isPlayer1Ready()).thenReturn(true);
        when(room.isPlayer2Ready()).thenReturn(true);

        gameWebSocketHandler.handleTextMessage(session, message);

        // "Start" 메시지가 전송되었는지 확인
        verify(session).sendMessage(argThat(textMessage -> textMessage.getPayload().equals("Start")));
        //verify(session).sendMessage(new TextMessage("Start")); // "Start" 메시지 확인
    }

    // "shoot" 메시지 처리 테스트
    @Test
    void testShoot() throws Exception {
        String messageContent = "shoot";
        TextMessage message = new TextMessage(messageContent);

        Player player = mock(Player.class);
        when(session.getAttributes()).thenReturn(Map.of("player", player)); // 세션에서 Player 가져오기

        gameWebSocketHandler.handleTextMessage(session, message);

        verify(player).shoot_Bullet(); // shoot_Bullet 호출 여부 확인
    }

    // "left" 메시지 처리 테스트
    @Test
    void testMovePlayer() throws Exception {
        String messageContent = "left";
        TextMessage message = new TextMessage(messageContent);

        Player player = mock(Player.class);
        when(session.getAttributes()).thenReturn(Map.of("player", player)); // 세션에서 Player 가져오기

        gameWebSocketHandler.handleTextMessage(session, message);

        verify(gameService).movePlayer(player, "left"); // movePlayer 호출 여부 확인
    }

    // 주기적으로 실행되는 removeClosedSession 테스트
    @Test
    void testRemoveClosedSession() throws Exception {
        gameWebSocketHandler.removeClosedSession();
        verify(roomRepository).deleteById(anyLong()); // 방 삭제 호출 확인
    }

    // 주기적으로 실행되는 update 테스트
    @Test
    void testUpdate() {
        gameWebSocketHandler.update();

        // moveBullets가 두 번 호출되었는지 확인 (두 플레이어가 각각의 총알을 이동시켜야 함)
        verify(gameService, times(2)).moveBullets(any(Player.class));
    }

    // 위치 정보가 주기적으로 업데이트되고 전송되는지 테스트
    @Test
    void testSendUpdatedPosition() throws Exception {
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);

        gameWebSocketHandler.sendUpdatedPosition(session1, session2);

        // 각 세션에 대한 위치 업데이트 메시지가 전송되었는지 확인
        verify(session1).sendMessage(any(TextMessage.class));
        verify(session2).sendMessage(any(TextMessage.class));
    }

    // WebSocket 연결 종료 후 세션 제거 테스트
    @Test
    void testAfterConnectionClosed() {
        gameWebSocketHandler.afterConnectionClosed(session, CloseStatus.NORMAL);
        verify(session).getAttributes(); // 세션 종료 후 속성 확인
    }
}
