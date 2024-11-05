package Invaders_SDP_server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static Invaders_SDP_server.Bullet.speed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // 플레이어의 위치 좌표 x, y
    private int x;
    private int y;
    private boolean direction; // player1(session1) 총알의 direction은 아래에서 위(True), player2(session2) 총알은 위에서 아래(False)
    private List<Bullet> bullets = new ArrayList<Bullet>();

    // 총알 발사 메소드 - 플레이어가 총알을 발사하면 bullets 리스트에 추가
    public void shoot_Bullet(boolean bullet_direction){
        Bullet bullet = new Bullet(this.x, this.y, bullet_direction);
        bullets.add(bullet);
    }

}
