package Invaders_SDP_server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 서버에서 총알 관리 (Player와는 독립적으로 작성)
public class Bullet {
    // 총알 위치 좌표, 속도, 방향
    private int x;
    private int y;
    private int speed;
    private String direction;

    // 총알 위치 업데이트 메소드
    public void updateBulletMove() {
        if("up".equals(direction)) { // session1(player1)은 아래에서 위로만 공격
            y -= speed;
        }
        else{  //session2(player2)는 위에서 아래로만 공격
            y += speed;
        }

    }

    // 총알과 상대 플레이어의 위치를 비교하여 충돌 여부 확인
    public boolean checkCollision(Player player) {
        int playerX = player.getX();
        int playerY = player.getY();

        // 총알과 플레이어의 위치 차이가 일정 거리 이내인지
        int collisionThreshold = 5;
        return Math.abs(this.x - playerX) < collisionThreshold &&
                Math.abs(this.y - playerY) < collisionThreshold;
    }

    // 총알이 화면 밖으로 나갔는지 확인하는 메소드
    public boolean isOutOfBounds() {
        return x < 0 || x > SCREEN_WIDTH || y < 0 || y > SCREEN_HEIGHT;
    }
}
