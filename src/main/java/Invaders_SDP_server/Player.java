package Invaders_SDP_server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // 플레이어의 위치 좌표 x, y
    private int x;
    private int y;
    private String direction;
    private List<Bullet> bullets = new ArrayList<Bullet>();

    // 총알 발사 메소드 - 플레이어가 총알을 발사하면 bullets 리스트에 추가
    public void shoot_Bullet(String bullet_direction){
        Bullet bullet = new Bullet(this.x, this.y, 5, bullet_direction);
        bullets.add(bullet);
    }

}
