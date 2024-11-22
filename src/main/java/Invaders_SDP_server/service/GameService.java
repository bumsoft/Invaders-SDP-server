package Invaders_SDP_server.service;

// 클라이언트가 WebSocket을 통해 서버로 정보를 전송한다
// 서버의 WebSocketHandler는 클라이언트로부터 전송된 정보를 수신하고 메세지 확인한다
// 수신된 방향 정보를 통해 어느 방향으로 얼만큼 이동할지 결정하고, 이를 gameservice로 전달하여
// 위치를 업데이트

import Invaders_SDP_server.data.Player;
import org.springframework.stereotype.Service;


// Service 클래스에서는 각 플레이어의 위치 업데이트와 조회 수행
@Service
public class GameService {

    // 입력 키에 따른 위치 정보 업데이트 - 화면을 벗어나는 경우 예외처리 필요!!!
    public void movePlayer(Player player, String msg){
        int x = player.getX();
        int y = player.getY();

        switch(msg){
            case "left":
                player.setX(x-10);
                break;
            case "right":
                player.setX(x+10);
                break;
        }
    }
}
