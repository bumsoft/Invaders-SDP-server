package Invaders_SDP_server.WebSocket;

import Invaders_SDP_server.dto.PositionDTO;
import Invaders_SDP_server.data.Bullet;
import Invaders_SDP_server.data.Player;
import Invaders_SDP_server.entity.Room;
import Invaders_SDP_server.repository.RoomRepository;
import Invaders_SDP_server.service.GameService;
import Invaders_SDP_server.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// 클라이언트로부터 좌표 데이터를 수신하고, 이를 다른 클라이언트에게 전송한다(양방향 전달)
// WebSocket 연결에서 발생하는 text메세지를 처리할 수 있게 함
@Component
@Slf4j
public class GameWebSocketHandler extends TextWebSocketHandler {

    // GameWebSocketHandler에서 클라이언트와 서버간의 실시간 소통 관리
    // 게임의 상태를 관리하는 GameService - 플레이어가 이동할 때 위치를 업데이트(movePlayer 메소드 내포)
    private final GameService gameService;

    final ObjectMapper objectMapper = new ObjectMapper();

    //연결된 모든 세션: sessions맵 생성 - session, player 객체 저장하여 관리
    private final Map<WebSocketSession, Player> sessions = new ConcurrentHashMap<>();

    //게임중인 방 세션모음, Long은 RoomId임
    private Map<Long, Set<WebSocketSession>> activeRoom = new ConcurrentHashMap<>();

    //대기중인 방 세션모음
    private Map<Long, Set<WebSocketSession>> waitingRoom = new ConcurrentHashMap<>();


    @Autowired
    RoomService roomService;
    @Autowired
    private RoomRepository roomRepository;

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
                if(created != null)
                {
                    Set<WebSocketSession> set = new HashSet();
                    set.add(session);
                    waitingRoom.put(created.getId(), set);

                    Player player = sessions.get(session);
                    player.setDirection(true); //방장은 아래서 시작.
                    session.sendMessage(new TextMessage("Created-" + created.getAccessCode()));
                }
                else //username이 없는 경우
                {
                    session.sendMessage(new TextMessage("ERROR-cannot find user"));
                    session.close(CloseStatus.SERVER_ERROR);
                }
            }
            case "join" -> {
                Room joinedRoom = roomService.joinRoom(parts[1],Long.parseLong(parts[2]));
                if(joinedRoom != null)
                {
                    waitingRoom.get(joinedRoom.getId()).add(session);

                    for(WebSocketSession ses : waitingRoom.get(joinedRoom.getId()))
                    {
                        ses.sendMessage(new TextMessage("Joined-"+joinedRoom.getPlayer1().getUsername()+"-"+joinedRoom.getPlayer2().getUsername()));
                    }//Joined-username1-username2반환(순서 상관x. 클라에서 처리하겠음)
                }
                else //조인실패
                {
                    session.sendMessage(new TextMessage("ERROR-cannot join room"));
                }
            }
            case "ready" -> {
                Room room = roomService.playerReady(parts[1]);
                if(room != null)
                {
                    if (room.isPlayer1Ready() && room.isPlayer2Ready())
                    {//둘다 레디일때
                        Set<WebSocketSession> set = waitingRoom.get(room.getId());
                        for (WebSocketSession ses : set)
                        {
                            ses.sendMessage(new TextMessage("Start"));
                        }
                        activeRoom.put(room.getId(), set); //실행방추가
                        waitingRoom.remove(room.getId()); //대기방 삭제
                    }
                }
            }
            case "shoot" -> { // 스페이스바 눌렀을 때 (총알 발사)
                Player player = sessions.get(session);
                player.shoot_Bullet();
            }
            default -> { //LEFT RIGHT 이동 처리
                Player player = sessions.get(session);
                gameService.movePlayer(player, parts[0]);
            }
        }


    }
    @Scheduled(fixedRate = 1000) //1초마다 유효하지 않은 대기방, 실행방, 엔티티 삭제
    @Transactional
    public void removeClosedSession()
    {
        for(Long roomId : activeRoom.keySet())
        {
            Set<WebSocketSession> set = activeRoom.get(roomId);
            for(WebSocketSession ses : set)
            {
                if(!sessions.containsKey(ses))
                {
                    activeRoom.remove(roomId);
                    roomRepository.removeById(roomId);
                }
            }
        }
        for(Long roomId : waitingRoom.keySet())
        {
            Set<WebSocketSession> set = waitingRoom.get(roomId);
            for(WebSocketSession ses : set)
            {
                if(!sessions.containsKey(ses))
                {
                    waitingRoom.remove(roomId);
                    roomRepository.removeById(roomId);
                }
            }
        }
    }

    // 주기적으로 서버에서 모든 클라이언트에게 최신화된 위치정보(플레이어 1,2, 총알 1,2) 전송
    @Scheduled(fixedRate = 16) //60FPS기준 16ms마다 갱신필요 필요
    public void sendUpdatedPositionToAll(){

        for (Set<WebSocketSession> sessions : activeRoom.values()) {
            //세션들(2개) 가져오기
            WebSocketSession[] sessionArray = sessions.toArray(new WebSocketSession[0]); //0으로 해두면 알아서.
            sendUpdatedPosition(sessionArray[0], sessionArray[1]);
        }
    }

    // 위치 정보 업데이트 메소드 - 양방향 동기화
    private void sendUpdatedPosition(WebSocketSession session1, WebSocketSession session2) {

        // 세션에 해당하는 Player 가져오기
        try
        {
            Player player1 = sessions.get(session1);
            Player player2 = sessions.get(session2);

            //위치 DTO 생성
            PositionDTO positionDTO1 = new PositionDTO(player1, player2);
            PositionDTO positionDTO2 = new PositionDTO(player2, player1);

            try
            {
                // DTO를 json으로
                String json1 = objectMapper.writeValueAsString(positionDTO1);
                String json2 = objectMapper.writeValueAsString(positionDTO2);

                session1.sendMessage(new TextMessage("UPDATE-" + json1));
                session2.sendMessage(new TextMessage("UPDATE-" + json2));

            } catch (Exception e)
            {
                log.info(e.getMessage());
                log.info("Error parsing positionDTO");
            }
        }catch(NullPointerException e){
            log.info("종료된세션에 대한 처리");
            return;
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
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
    {
        log.info("클라이언트에서 연결을 종료함");
        sessions.remove(session);
    }
}

