package Invaders_SDP_server.WebSocket;

import Invaders_SDP_server.User.dto.BulletPositionDTO;
import Invaders_SDP_server.User.dto.GameStateDTO;
import Invaders_SDP_server.User.dto.PositionDTO;
import Invaders_SDP_server.User.data.Bullet;
import Invaders_SDP_server.User.data.Player;
import Invaders_SDP_server.User.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// 클라이언트로부터 좌표 데이터를 수신하고, 이를 다른 클라이언트에게 전송한다(양방향 전달)
// WebSocket 연결에서 발생하는 text메세지를 처리할 수 있게 함
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    // GameWebSocketHandler에서 클라이언트와 서버간의 실시간 소통 관리
    // 게임의 상태를 관리하는 GameService - 플레이어가 이동할 때 위치를 업데이트(movePlayer 메소드 내포)
    private final GameService gameService;

    // DTO을 json 형태로 변환
    final ObjectMapper objectMapper = new ObjectMapper();

    // 두명의 클라이언트를 위한 WebSocketSession session1, session2의 위치 정보를 따로 관리
    // WebSocketSession은 클라이언트와 서버 간의 연결을 나타내는 객체 (key로 사용)
    private WebSocketSession session1;
    private WebSocketSession session2;


    // sessions맵 생성 - session i, player 객체 저장하여 관리
    private final Map<WebSocketSession, Player> sessions = new ConcurrentHashMap<>();

    // 생성자 - GameService에 의존함
    @Autowired
    public GameWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String msg = message.getPayload().toLowerCase();

        switch (msg) {
            case "shoot" -> { // 스페이스바 눌렀을 때 (총알 발사)
                Player player = sessions.get(session);
                if (session.equals(session1)) {
                    player.shoot_Bullet(true); // 위로 발사
                } else if (session.equals(session2)) {
                    player.shoot_Bullet(false); // 아래로 발사
                }
            }
            case "stopshoot" -> { // 스페이스바를 뗐을 때 (총알 발사 중지)
                Player player = sessions.get(session);
                player.stopShooting();
            }
            case "stop" -> { // 이동을 멈췄을 때
                Player player = sessions.get(session);
                player.stopMoving();
            }
            default -> { // a, w, s, d 키 입력으로 이동 처리
                if (session.equals(session1) && session2 != null) {
                    Player player1 = sessions.get(session1);
                    gameService.movePlayer(player1, msg);
                } else if (session.equals(session2) && session1 != null) {
                    Player player2 = sessions.get(session2);
                    gameService.movePlayer(player2, msg);
                }
            }
        }

    }
    // 주기적으로 서버에서 모든 클라이언트에게 최신화된 위치정보(플레이어 1,2, 총알 1,2) 전송
    @Scheduled(fixedRate = 1000) // 1초마다 서버가 클라이언트에게 정보 전송
    public void sendUpdatedPositionToAll(){
        sessions.keySet().forEach(this::sendUpdatedPosition);

    }

    // 위치 정보 업데이트 메소드 - 양방향 동기화
    private void sendUpdatedPosition(WebSocketSession session) {
        Player player = sessions.get(session);
        Player enemyPlayer = session.equals(session1) ? sessions.get(session2) : sessions.get(session1);

        // 두 클라이언트가 모두 연결된 경우
        if (player != null && enemyPlayer != null) {
            // Player의 위치 DTO 생성
            PositionDTO positionDTO = new PositionDTO(player, enemyPlayer);

            // Player의 총알 위치를 BulletPositionDTO로 변환
            List<BulletPositionDTO> playerBullets = player.getBullets().stream().map
                    (bullet -> new BulletPositionDTO(bullet.getX(), bullet.getY(), bullet.isDirection())).collect(Collectors.toList());

            // EnemyPlayer의 총알 위치를 BulletPositionDTO로 변환
            List<BulletPositionDTO> enemyBullets = enemyPlayer.getBullets().stream().map(
                    bullet -> new BulletPositionDTO(bullet.getX(), bullet.getY(), bullet.isDirection())).collect(Collectors.toList());

            // player, enemyPlayer의 위치, 각 player의 bullet의 위치 정보 모두 내포
            GameStateDTO gameStateDTO = new GameStateDTO(positionDTO, playerBullets, enemyBullets);

            try {
                // DTO를 json으로
                String json = objectMapper.writeValueAsString(positionDTO);

                session.sendMessage(new TextMessage(json));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

// 총알 삭제 메소드 (총알이 상대와 충돌 혹은 화면 밖으로 나간 경우 삭제 처리)
private void removeOffScreenAndCollidingBullets(Player player, Player enemyPlayer){
                // Player의 총알이 enemyPlayer와 충돌이 났는지 확인
                Iterator<Bullet> iterator1 = player.getBullets().iterator();
                while (iterator1.hasNext()) {
                    Bullet bullet = iterator1.next();
                    // 충돌 혹은 화면 밖으로 나가면 삭제
                    if (bullet.checkCollision(enemyPlayer) || bullet.isOutOfBounds()) {
                        iterator1.remove();
                    }
                }

                // Player2의 총알이 PLayer1과 충돌이 났는지 확인
                Iterator<Bullet> iterator2 = enemyPlayer.getBullets().iterator();
                while (iterator2.hasNext()) {
                    Bullet bullet = iterator2.next();
                    // 충돌 혹은 화면 밖으로 나가면 삭제
                    if (bullet.checkCollision(enemyPlayer) || bullet.isOutOfBounds()) { // 충돌 났으면 제거
                        iterator2.remove();
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

    // 클라이언트가 연결을 종료한 경우
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        sessions.remove(session);
        if(session.equals(session1)){
            session1 = null;
        }
        else if(session.equals(session2)){
            session2 = null;
        }
    }

}

