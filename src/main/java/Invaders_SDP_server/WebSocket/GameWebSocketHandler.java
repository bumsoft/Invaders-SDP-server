package Invaders_SDP_server.WebSocket;

import Invaders_SDP_server.dto.BulletPositionDTO;
import Invaders_SDP_server.dto.GameStateDTO;
import Invaders_SDP_server.dto.PositionDTO;
import Invaders_SDP_server.data.Bullet;
import Invaders_SDP_server.data.Player;
import Invaders_SDP_server.entity.Room;
import Invaders_SDP_server.entity.User;
import Invaders_SDP_server.service.GameService;
import Invaders_SDP_server.service.RoomService;
import Invaders_SDP_server.service.RoomService.RoomStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// 클라이언트로부터 좌표 데이터를 수신하고, 이를 다른 클라이언트에게 전송한다(양방향 전달)
// WebSocket 연결에서 발생하는 text메세지를 처리할 수 있게 함
@Component
@Slf4j
public class GameWebSocketHandler extends TextWebSocketHandler {

    // GameWebSocketHandler에서 클라이언트와 서버간의 실시간 소통 관리
    // 게임의 상태를 관리하는 GameService - 플레이어가 이동할 때 위치를 업데이트(movePlayer 메소드 내포)
    private final GameService gameService;

    // DTO을 json 형태로 변환
    final ObjectMapper objectMapper = new ObjectMapper();

    // sessions맵 생성 - session i, player 객체 저장하여 관리
    private final Map<WebSocketSession, Player> sessions = new ConcurrentHashMap<>();

    //방 세션모음
    private Map<Long, Set<WebSocketSession>> activeRoom = new ConcurrentHashMap<>();

    // 방 별로 세션, 상태 관리
    private final Map<String, WebSocketSession> roomSessions = new ConcurrentHashMap<>();
    //역맵
    private final Map<WebSocketSession, String> rvRoomSessions = new ConcurrentHashMap<>();

    @Autowired
    RoomService roomService;

    // 생성자 - GameService에 의존함
    @Autowired
    public GameWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String msg = message.getPayload().toLowerCase();
        //msg는 메서드-사용자이름형태-optional형태
        //msg를 -기준으로 나누는 작업
        String[] parts = msg.split("-");
        switch (parts[0]) {
            case "create" -> {
                Room created = roomService.createRoom(parts[1]);
                roomSessions.put(created.getPlayer1().getUsername(),session);
                rvRoomSessions.put(session,created.getPlayer1().getUsername());
                session.sendMessage(new TextMessage("Created-"+created.getAccessCode()));
            }
            case "join" -> {
                Room updated = roomService.joinRoom(parts[1],Long.parseLong(parts[2]));
                if(updated == null){
                    session.sendMessage(new TextMessage("JoinedFailed-Full"));
                }
                else{
                    roomSessions.put(updated.getPlayer2().getUsername(),session);
                    rvRoomSessions.put(session,updated.getPlayer2().getUsername());
                    session.sendMessage(new TextMessage("Joined-"+updated.getPlayer1().getUsername()));
                    roomSessions.get(updated.getPlayer1().getUsername()).sendMessage(new TextMessage("Joined-"+updated.getPlayer2().getUsername()));
                }
            }
            case "ready" -> {
                Room updated = roomService.playerReady(parts[1]);
                if(updated.isPlayer1Ready() && updated.isPlayer2Ready()){
                    //둘다 레디일때
                    roomSessions.get(updated.getPlayer1().getUsername()).sendMessage(new TextMessage("Start"));
                    roomSessions.get(updated.getPlayer2().getUsername()).sendMessage(new TextMessage("Start"));

                    Set<WebSocketSession> set = new HashSet();
                    set.add(roomSessions.get(updated.getPlayer1().getUsername()));
                    set.add( roomSessions.get(updated.getPlayer2().getUsername()));
                    activeRoom.put(updated.getId(), set);


                }
                else if(updated.isPlayer1Ready()){
                    roomSessions.get(updated.getPlayer2().getUsername()).sendMessage(new TextMessage("Ready-"+updated.getPlayer1().getUsername()));
                }
                else {
                    roomSessions.get(updated.getPlayer1().getUsername()).sendMessage(new TextMessage("Ready-"+updated.getPlayer2().getUsername()));
                }
            }
            // 서버로 사용자 이름과 명령 전달
            case "shoot" -> { // 스페이스바 눌렀을 때 (총알 발사)
                Player player = sessions.get(session);
                RoomStatus gameRoom = roomService.getRoom(parts[1]);
                //Room gameRoom = roomService.getRoom(parts[1]).room();
                switch (gameRoom.player()){
                    case 1 -> {
                        player.shoot_Bullet(true);
                    }
                    case 2 -> {
                        player.shoot_Bullet(false);
                    }
                    default -> {

                    }
                }
            }
            case "stopshoot" -> { // 스페이스바를 뗐을 때 (총알 발사 중지)
                Player player = sessions.get(session);
                RoomStatus gameRoom = roomService.getRoom(parts[1]);
                switch (gameRoom.player()){
                    case 1, 2 -> {
                        player.stopShooting();
                    }
                    default -> {

                    }
                }

            }
            case "stop" -> { // 이동을 멈췄을 때
                Player player = sessions.get(session);
                RoomStatus gameRoom = roomService.getRoom(parts[1]);
                switch (gameRoom.player()){
                    case 1, 2 -> {
                        player.stopMoving();
                    }
                    default -> {
                        
                    }
                }

            }
            default -> { // a, w, s, d 키 입력으로 이동 처리
                Player player = sessions.get(session);
                RoomStatus gameRoom = roomService.getRoom(parts[1]);
                switch (gameRoom.player()){
                    case 1, 2 -> {
                        gameService.movePlayer(player, parts[0]);
                    }
                }

            }
        }


    }

    // 주기적으로 서버에서 모든 클라이언트에게 최신화된 위치정보(플레이어 1,2, 총알 1,2) 전송
    @Scheduled(fixedRate = 10) // 0.01초마다 서버가 클라이언트에게 정보 전송
    public void sendUpdatedPositionToAll(){

        for (Set<WebSocketSession> sessions : activeRoom.values()) {
            //세션들(2개) 가져오기
            WebSocketSession[] sessionArray = sessions.toArray(new WebSocketSession[2]);
            sendUpdatedPosition(sessionArray[0], sessionArray[1]);

        }
        System.out.println("sendUpdatedPositionToAll");
    }

    // 위치 정보 업데이트 메소드 - 양방향 동기화
    private void sendUpdatedPosition(WebSocketSession session1, WebSocketSession session2) {

        // 세션에 해당하는 Player 가져오기
        Player player1 = sessions.get(session1);
        Player player2 = sessions.get(session2);


        // 두 클라이언트가 모두 연결된 경우
        if (player1 != null && player2 != null) {
            // Player의 위치 DTO 생성
            PositionDTO positionDTO1 = new PositionDTO(player1, player2);
            //
            PositionDTO positionDTO2 = new PositionDTO(player2, player1);

            // Player의 총알 위치를 BulletPositionDTO로 변환
            //List<BulletPositionDTO> playerBullets = player1.getBullets().stream().map
            //        (bullet -> new BulletPositionDTO(bullet.getX(), bullet.getY(), bullet.isDirection())).collect(Collectors.toList());

            // EnemyPlayer의 총알 위치를 BulletPositionDTO로 변환
            //List<BulletPositionDTO> enemyBullets = player2.getBullets().stream().map(
            //        bullet -> new BulletPositionDTO(bullet.getX(), bullet.getY(), bullet.isDirection())).collect(Collectors.toList());

            // player, enemyPlayer의 위치, 각 player의 bullet의 위치 정보 모두 내포
            //GameStateDTO gameStateDTO = new GameStateDTO(positionDTO1, playerBullets, enemyBullets);

            try {
                // DTO를 json으로
                String json1 = objectMapper.writeValueAsString(positionDTO1);
                String json2 = objectMapper.writeValueAsString(positionDTO2);

                session1.sendMessage(new TextMessage("UPDATE-"+json1));
                session2.sendMessage(new TextMessage("UPDATE-"+json2));

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
    }

    // 클라이언트가 연결을 종료한 경우
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        log.info("종료");
        String currentUsername = rvRoomSessions.get(session);
        String enemyUsername = "";
        RoomStatus target = roomService.getRoom(currentUsername);

        activeRoom.remove(target.room().getId()); // 활성된룸 삭제
        rvRoomSessions.remove(session);
        sessions.remove(session);
        roomService.deleteRoom(target.room().getPlayer1().getUsername());
        switch(target.player()){
            case 1 -> {
                try{
                    enemyUsername = target.room().getPlayer2().getUsername();
                }
                catch (Exception e){

                }
            }
            case 2 -> {
                try{
                    enemyUsername = target.room().getPlayer1().getUsername();
                }
                catch (Exception e){

                }
            }
            default -> {

            }
        }
        try{
            roomSessions.get(enemyUsername).sendMessage(new TextMessage("GameClosed-enemy exit"));
            roomSessions.get(enemyUsername).close();
            rvRoomSessions.remove(roomSessions.get(enemyUsername));
            sessions.remove(roomSessions.get(enemyUsername));
        }
        catch (Exception e) {

        }
        roomSessions.remove(target.room().getPlayer1().getUsername());
        roomSessions.remove(target.room().getPlayer2().getUsername());
    }

}

