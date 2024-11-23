package Invaders_SDP_server.service;

// 클라이언트가 WebSocket을 통해 서버로 정보를 전송한다
// 서버의 WebSocketHandler는 클라이언트로부터 전송된 정보를 수신하고 메세지 확인한다
// 수신된 방향 정보를 통해 어느 방향으로 얼만큼 이동할지 결정하고, 이를 gameservice로 전달하여
// 위치를 업데이트

import Invaders_SDP_server.data.Bullet;
import Invaders_SDP_server.data.Player;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


// Service 클래스에서는 각 플레이어의 위치 업데이트와 조회 수행
@Service
public class GameService {

    // WebSocket 메시지를 전송하기 위한 템플릿
    private final SimpMessagingTemplate messagingTemplate;

    // SimpMessagingTemplate을 통해 WebSocket 메시지 전송
    public GameService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 입력 키에 따른 위치 정보 업데이트 - 화면을 벗어나는 경우 예외처리 필요!!!
    public void movePlayer(Player player, String msg){
        int x = player.getX();
        int y = player.getY();

        switch(msg){
            case "left":
                if(x-10 >0) // 왼쪽 경계
                    player.setX(x-10);
                break;
            case "right":
                if(x+10 < 600) // 오른쪽 경계
                    player.setX(x+10);
                break;
            case "up":
                if (y - 10 > 0) // 위쪽 경계
                    player.setY(y - 10);
                break;
            case "down":
                if (y + 10 < 600) // 아래쪽 경계
                    player.setY(y + 10);
                break;    
        }
    }

    public void moveBullets(Player player)
    {
        List<Bullet> bullets = player.getBullets();
        // 총알이 화면 밖으로 벗어나면 제거
        bullets.removeIf(bullet -> bullet.getY() < 0 || bullet.getY() > 600);

        if(player.isDirection()) //위로
        {
            for(Bullet bullet : bullets)
            {
                bullet.setY(bullet.getY()-5);
            }
        }
        else//아래로
        {
            for(Bullet bullet : bullets)
            {
                bullet.setY(bullet.getY()+5);
            }
        }
    }


    // 플레이어와 총알 충돌 확인 메소드 - Player에 getBounds() 메소드 추가
    public boolean checkCollision(Player player, List<Bullet> enemyBullets) {
        // null 또는 비어 있는 리스트 확인
        if (enemyBullets == null || enemyBullets.isEmpty()) {
            System.out.println("No bullets to check for collision");
            return false;
        }
        
        for (Bullet bullet : enemyBullets) {
            if (player.getBounds().intersects(bullet.getBounds())) { // 충돌 감지
                return true; // 충돌 발생
            }
        }
    return false;
    }

    // 충돌 발생 시 게임 종료 로직
    public void handleGameOver(Player player) {
        System.out.println("Game Over for player: " + player.getUsername());

        // WebSocket을 통해 게임 종료 메시지 전송
        String message = "Game Over for player: " + player.getUsername();
        messagingTemplate.convertAndSend("/topic/game-over", message);

        // 추가 작업: 필요 시 게임 상태 초기화 또는 데이터 저장
    }

    // 게임 업데이트 메서드
    public void updateGame(Player player1, Player player2, List<Bullet> player1Bullets, List<Bullet> player2Bullets) {
        // 각 플레이어의 총알 이동 업데이트
        moveBullets(player1);
        moveBullets(player2);

        // Player1이 Player2의 총알에 맞은 경우 게임 종료 처리
        if (checkCollision(player1, player2Bullets)) {
            handleGameOver(player1); // Player1 충돌 처리
        }
        // Player2가 Player1의 총알에 맞은 경우 게임 종료 처리
        if (checkCollision(player2, player1Bullets)) {
            handleGameOver(player2); // Player2 충돌 처리
        }
    }

}
