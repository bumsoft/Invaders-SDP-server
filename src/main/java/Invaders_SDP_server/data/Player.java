package Invaders_SDP_server.data;

import Invaders_SDP_server.dto.BulletPositionDTO;
import Invaders_SDP_server.entity.User;
import lombok.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    // username을 가져오기 위한
    private User user;

    // 플레이어의 위치 좌표 x, y
    private int x=325; //중앙
    private int y=100; //게임시작시 바꿔줌
    private int width = 10; // 플레이어의 너비
    private int height = 10; // 플레이어의 높이

    private boolean direction = false; //방장true 참가자 false로 해둠.; true면 아래서 시작/false면 위에서 시작
    private List<Bullet> bullets = new ArrayList<Bullet>();

    // Getter for username (User의 username 반환)
    public String getUsername() {
        return user.getUsername(); // User 객체의 username 반환
    }

    // 총알 발사 메소드 - 플레이어가 총알을 발사하면 bullets 리스트에 추가
    public void shoot_Bullet(){
        Bullet bullet = new Bullet(this.x, this.y, this.direction); // 플레이어의 중앙에서 발사 시작
        bullets.add(bullet);
    }
    public List<BulletPositionDTO> getDTO()
    {
        List<BulletPositionDTO> list = new ArrayList<>();
        for(Bullet bullet : bullets)
        {
            list.add(new BulletPositionDTO(bullet.getX(), bullet.getY()));
        }
        return list;
    }

    // 충돌 감지를 위한 계산
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

}
