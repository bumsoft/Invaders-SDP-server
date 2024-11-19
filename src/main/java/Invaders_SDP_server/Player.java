package Invaders_SDP_server;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static Invaders_SDP_server.Bullet.speed;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // 플레이어의 위치 좌표 x, y
    private int x;
    private int y;
    private boolean direction; // player1(session1) 총알의 direction은 아래에서 위(True), player2(session2) 총알은 위에서 아래(False)
    private List<Bullet> bullets = new ArrayList<Bullet>();

    private boolean isMoving = false;
    private boolean isShooting = false;

    // 총알 발사 메소드 - 플레이어가 총알을 발사하면 bullets 리스트에 추가
    public void shoot_Bullet(boolean bullet_direction){
        isShooting = true;
        Bullet bullet = new Bullet(this.x, this.y, bullet_direction);
        bullets.add(bullet);
    }

    public void stopShooting() {
        isShooting = false;
        // 발사 중단 로직 필요 시 작성
    }

    // 이동 메서드
    public void move(String direction) {
        isMoving = true;
        // 이동 로직
        switch (direction.toLowerCase()) {
            case "a" -> x -= speed; // 왼쪽 이동
            case "d" -> x += speed; // 오른쪽 이동
            case "w" -> y -= speed; // 위로 이동
            case "s" -> y += speed; // 아래로 이동
        }
    }

    // 이동을 멈추는 메서드
    public void stopMoving() {
        isMoving = false;
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

}
