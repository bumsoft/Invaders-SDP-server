package Invaders_SDP_server.data;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static Invaders_SDP_server.data.Bullet.speed;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // 플레이어의 위치 좌표 x, y
    private int x=100; //임시값
    private int y=100;
    private boolean direction = false; //방장true 참가자 false로 해둠.; true면 아래서 시작/false면 위에서 시작
    private List<Bullet> bullets = new ArrayList<Bullet>();

    // 총알 발사 메소드 - 플레이어가 총알을 발사하면 bullets 리스트에 추가
    public void shoot_Bullet(){
        Bullet bullet = new Bullet(this.x, this.y, this.direction); // 플레이어의 중앙에서 발사 시작
        bullets.add(bullet);
    }

}
