package Invaders_SDP_server.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static Invaders_SDP_server.data.Bullet.speed;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // 플레이어의 위치 좌표 x, y
    private int x=100; //임시값
    private int y=100;
    private boolean direction; // player1(session1) 총알의 direction은 아래에서 위(True), player2(session2) 총알은 위에서 아래(False)
    private List<Bullet> bullets = new ArrayList<Bullet>();

    private boolean isMoving = false;
    private boolean isShooting = false;

    // 총알 발사 메소드 - 플레이어가 총알을 발사하면 bullets 리스트에 추가
    public void shoot_Bullet(boolean bullet_direction){
        isShooting = true;
        Bullet bullet = new Bullet(this.x + 22, this.y, bullet_direction); // 플레이어의 중앙에서 발사 시작
        bullets.add(bullet);
        System.out.println("Bullet created at (" + this.x + ", " + this.y + ")"); // 디버깅 로그
    }

    public void stopShooting() {
        isShooting = false;
        // 발사 중단 로직 필요 시 작성
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
