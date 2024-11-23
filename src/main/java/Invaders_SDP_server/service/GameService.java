package Invaders_SDP_server.service;

// 클라이언트가 WebSocket을 통해 서버로 정보를 전송한다
// 서버의 WebSocketHandler는 클라이언트로부터 전송된 정보를 수신하고 메세지 확인한다
// 수신된 방향 정보를 통해 어느 방향으로 얼만큼 이동할지 결정하고, 이를 gameservice로 전달하여
// 위치를 업데이트

import Invaders_SDP_server.data.Bullet;
import Invaders_SDP_server.data.Player;
import org.springframework.stereotype.Service;

import java.util.List;


// Service 클래스에서는 각 플레이어의 위치 업데이트와 조회 수행
@Service
public class GameService {

    // 입력 키에 따른 위치 정보 업데이트 - 화면을 벗어나는 경우 예외처리 필요!!!
    public void movePlayer(Player player, String msg){
        int x = player.getX();
        int y = player.getY();

        switch(msg){
            case "left":
                if(x-7 >0) // 왼쪽 경계
                    player.setX(x-7);
                else
                    player.setX(0);
                break;
            case "right":
                if(x+7 < 600-42) // 오른쪽 경계
                    player.setX(x+7);
                else
                    player.setX(600-42);
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
                bullet.setY(bullet.getY()-20);
            }
        }
        else//아래로
        {
            for(Bullet bullet : bullets)
            {
                bullet.setY(bullet.getY()+20);
            }
        }
    }


    // 플레이어와 총알 충돌 확인 메소드 - Player에 getBounds() 메소드 추가
    public boolean checkCollision(Player player, List<Bullet> enemyBullets) {
        // null 또는 비어 있는 리스트 확인
        if (enemyBullets == null || enemyBullets.isEmpty()) {
            return false;
        }
        
        for (Bullet bullet : enemyBullets) {
//            if (player.getBounds().intersects(bullet.getBounds())) { // 충돌 감지
//                return true; // 충돌 발생
            int centerAX = player.getX() + player.getWidth() / 2;
            int centerAY = player.getY() + player.getHeight() / 2;

            int centerBX = bullet.getX() + bullet.getWidth() / 2;
            int centerBY = bullet.getY() + bullet.getHeight() / 2;
            // Calculate maximum distance without collision.
            int maxDistanceX = player.getWidth() / 2 + bullet.getWidth() / 2;
            int maxDistanceY = player.getHeight() / 2 + bullet.getHeight() / 2;
            // Calculates distance.
            int distanceX = Math.abs(centerAX - centerBX);
            int distanceY = Math.abs(centerAY - centerBY);

            if(distanceX < maxDistanceX && distanceY < maxDistanceY)
                return true;
        }
    return false;
    }

    // 게임 업데이트 메서드
    /* public void updateGame(Player player1, Player player2, List<Bullet> player1Bullets, List<Bullet> player2Bullets) {
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
    } */

    // 게임 종료 처리
    public void handleGameOver(Player player) {
        System.out.println("Game Over for player: " + player.getUsername());
    }

}
