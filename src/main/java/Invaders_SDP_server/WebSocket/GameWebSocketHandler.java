package Invaders_SDP_server.WebSocket;

import Invaders_SDP_server.dto.GameStateDTO;
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

import java.io.IOException;
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
                            Player player = sessions.get(ses);
                            player.setY(player.isDirection()?600:50);
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
    public void removeClosedSession() throws IOException
    {
        List<Long> roomsToRemove = new ArrayList<>();
        for(Long roomId : activeRoom.keySet())
        {
            Set<WebSocketSession> set = activeRoom.get(roomId);
            if(set==null || set.isEmpty())
            {
                roomsToRemove.add(roomId);
                continue;
            }
            for(WebSocketSession ses : set)
            {
                if(!sessions.containsKey(ses))
                {
                    roomsToRemove.add(roomId);
                    break;
                }
            }
        }
        for(Long roomId : waitingRoom.keySet())
        {
            Set<WebSocketSession> set = waitingRoom.get(roomId);
            if(set==null || set.isEmpty())
            {
                roomsToRemove.add(roomId);
                continue;
            }
            for(WebSocketSession ses : set)
            {
                if(!sessions.containsKey(ses))
                {
                    roomsToRemove.add(roomId);
                    break;
                }
            }
        }
        for(Long roomId : roomsToRemove)
        {
            Set<WebSocketSession> set1 = activeRoom.get(roomId);
            if(set1 != null)
            {
                for (WebSocketSession ses : set1)
                {
                    if (ses.isOpen())
                    {
                        ses.sendMessage(new TextMessage("ERROR-상대의 게임종료"));
                    }
                }
            }
            Set<WebSocketSession> set2 = waitingRoom.get(roomId);
            if(set2 != null)
            {
                for (WebSocketSession ses : set2)
                {
                    if (ses.isOpen())
                    {
                        ses.sendMessage(new TextMessage("ERROR-상대의 게임종료"));
                    }
                }
            }
            activeRoom.remove(roomId);
            waitingRoom.remove(roomId);
            roomRepository.deleteById(roomId);
        }
    }

    //총알위치 업뎃. 충돌처리도 여기서 할 것
    @Scheduled(fixedRate = 100)
    public void update()
    {
        for (Set<WebSocketSession> sessions : activeRoom.values())
        {
            //세션들(2개) 가져오기
            WebSocketSession[] sessionArray = sessions.toArray(new WebSocketSession[0]); //0으로 해두면 알아서.
            if(sessionArray.length == 0) continue;
            Player player1 = this.sessions.get(sessionArray[0]);
            Player player2 = this.sessions.get(sessionArray[1]);
            if(player1 == null || player2 == null)
            {
                this.sessions.remove(sessionArray[0]);
                this.sessions.remove(sessionArray[1]);
                continue;
            }
            gameService.moveBullets(player1);
            gameService.moveBullets(player2);

            try {
                if (isPlayer1Over(player1, player2)) {
                    try {
                        sessionArray[0].sendMessage(new TextMessage("GAMEOVER-LOSE"));
                        sessionArray[1].sendMessage(new TextMessage("GAMEOVER-WIN"));
                    } catch (Exception e) {
                        log.error("Error sending game over message for Player 1", e);
                    }
                    continue;
                }
                if (isPlayer2Over(player1, player2)) {
                    try {
                        sessionArray[0].sendMessage(new TextMessage("GAMEOVER-WIN"));
                        sessionArray[1].sendMessage(new TextMessage("GAMEOVER-LOSE"));
                    } catch (Exception e) {
                        log.error("Error sending game over message for Player 2", e);
                    }
                    continue;
                }
            } catch (Exception e) {
                log.error("Error during collision detection", e);
            }

        }
    }

    // 주기적으로 서버에서 모든 클라이언트에게 최신화된 위치정보(플레이어 1,2, 총알 1,2) 전송
    @Scheduled(fixedRate = 100) //60FPS기준 16ms마다 갱신필요 필요
    public void sendUpdatedPositionToAll(){

        for (Set<WebSocketSession> sessions : activeRoom.values()) {
            //세션들(2개) 가져오기
            WebSocketSession[] sessionArray = sessions.toArray(new WebSocketSession[0]); //0으로 해두면 알아서.
            if(sessionArray.length==0) continue;
            sendUpdatedPosition(sessionArray[0], sessionArray[1]);
        }
    }

    // 위치 정보 업데이트 메소드 - 양방향 동기화
    private void sendUpdatedPosition(WebSocketSession session1, WebSocketSession session2) {

        try
        {
            // 세션에 해당하는 Player 가져오기
            try
            {
                Player player1 = sessions.get(session1);
                Player player2 = sessions.get(session2);


                GameStateDTO gameStateDTO1 = GameStateDTO.builder()
                        .player1X(player1.getX())
                        .player1Y(player1.getY())
                        .player1BulletPositionDTO(player1.getDTO())
                        .player2X(player2.getX())
                        .player2Y(player2.getY())
                        .player2BulletPositionDTO(player2.getDTO())
                        .build();

                GameStateDTO gameStateDTO2 = GameStateDTO.builder()
                        .player1X(player2.getX())
                        .player1Y(player2.getY())
                        .player1BulletPositionDTO(gameStateDTO1.getPlayer2BulletPositionDTO())
                        .player2X(player1.getX())
                        .player2Y(player1.getY())
                        .player2BulletPositionDTO(gameStateDTO1.getPlayer1BulletPositionDTO())
                        .build();
                try
                {
                    // DTO를 json으로
                    String json1 = objectMapper.writeValueAsString(gameStateDTO1);
                    String json2 = objectMapper.writeValueAsString(gameStateDTO2);

                    session1.sendMessage(new TextMessage("UPDATE-" + json1));
                    session2.sendMessage(new TextMessage("UPDATE-" + json2));

                } catch (Exception e)
                {
                    log.info(e.getMessage());
                    log.info("Error parsing positionDTO");
                }
            } catch (NullPointerException e)
            {
                log.info("종료된세션에 대한 처리");
                return;
            }
        }catch(NullPointerException e)
        {
            log.info(e.getMessage());
            log.info("종료된세션에 대한 처리");
        }
    }

    // Player1이 충돌했는지 확인
    private boolean isPlayer1Over(Player player1, Player player2) {
        return gameService.checkCollision(player1, player2.getBullets());
    }

    // Player2가 충돌했는지 확인
    private boolean isPlayer2Over(Player player1, Player player2) {
        return gameService.checkCollision(player2, player1.getBullets());
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

