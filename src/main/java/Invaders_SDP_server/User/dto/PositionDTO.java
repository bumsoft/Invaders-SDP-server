package Invaders_SDP_server.User.dto;

import Invaders_SDP_server.User.entity.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
@AllArgsConstructor
public class PositionDTO {

    private int playerX;
    private int playerY;
    private int enemyPlayerX;
    private int enemyPlayerY;

    // Player 객체를 받아서 위치 정보를 설정하는 생성자
    public PositionDTO(Player player, Player enemyPlayer) {
        this.playerX = player.getX();
        this.playerY = player.getY();
        this.enemyPlayerX = enemyPlayer.getX();
        this.enemyPlayerY = enemyPlayer.getY();
    }

}
