package Invaders_SDP_server;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// 클라이언트로부터 좌표 데이터를 수신하고, 이를 다른 클라이언트에게 전송한다(양방향 전달)
// WebSocket 연결에서 발생하는 text메세지를 처리할 수 있게 함
public class GameWebSocketHandler extends TextWebSocketHandler {
    // GameWebSocketHandler에서 클라이언트와 서버간의 실시간 소통 관리
    // 게임의 상태를 관리하는 GameService - 플레이어가 이동할 때 위치를 업데이트(movePlayer 메소드 내포)
    GameService gameService;

    // 두명의 클라이언트를 위한 WebSocketSession session1, session2의 위치 정보를 따로 관리
    // WebSocketSession은 클라이언트와 서버 간의 연결을 나타내는 객체 (key로 사용)
    private WebSocketSession session1;
    private WebSocketSession session2;

    // sessions맵 생성 - session i, player 객체 저장하여 관리
    private final Map<WebSocketSession, Player> sessions = new ConcurrentHashMap<>();

    // 생성자 - GameService에 의존함
    public GameWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String msg = message.getPayload().toLowerCase();

            // 클라이언트로부터 GET요청을 받았을 때 서버에서 최신화된 위치 정보를 GET요청을 보낸 클라이언트에게 전송
            if("GET".equals(msg)) {
                // session1이 GET요청을 보낸 경우
                if(session.equals(session1) && session2 != null) {
                    sendUpdatedPosition(session1, session2);
                }
                // session2가 GET요청을 보낸 경우
                else if(session.equals(session2) && session1 != null) {
                    sendUpdatedPosition(session2, session1);
                }
            }
            else{ // 클라이언트로부터 a,w,s,d 입력을 받은 경우 서버 내에서만 위치좌표 최신화
                if(session.equals(session1) && session2 != null){
                    Player player1 = sessions.get(session1);
                    gameService.movePlayer(player1, msg);
                }
                else if(session.equals(session2) && session1 != null){
                    Player player2 = sessions.get(session2);
                    gameService.movePlayer(player2, msg);

                }
            }

    }

    // 위치 정보 업데이트 메소드 - 양방향 동기화
    private void sendUpdatedPosition(WebSocketSession session1, WebSocketSession session2){
        // 두 클라이언트가 모두 연결된 경우
        if(session1 != null && session2 != null){
            Player player1 = sessions.get(session1);
            Player player2 = sessions.get(session2);

            // player1의 위치 정보를 player1과 player2에게 전송
            try{
                session1.sendMessage(new TextMessage(
                        String.format("x:%d,y:%d;enemy_x:%d,enemy_y:%d",
                                player1.getX(), player1.getY(), player2.getX(), player2.getY())
                ));
                session2.sendMessage(new TextMessage(
                        String.format("x:%d,y:%d;enemy_x:%d,enemy_y:%d",
                                player2.getX(), player2.getY(), player1.getX(), player1.getY())
                ));
            }catch(Exception e){
                e.printStackTrace();
            }
        }


    }

    // 새로운 클라이언트가 연결된 경우
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        // 새로운 클라이언트가 등록될 때마다 초기 위치 좌표값 부여
        // sessions 맵에 저장
        Player newPlayer = new Player();
        sessions.put(session, newPlayer);

        // 새로운 세션 할당
        if(session1 == null){
            session1 = session;
        }
        else if(session2 == null){
            session2 = session;
        }
        else{
            try{
                session.sendMessage(new TextMessage("Game session is full. Please try again later."));
                session.close();
            }catch(Exception e){e.printStackTrace();}
        }
        return;
    }

}

