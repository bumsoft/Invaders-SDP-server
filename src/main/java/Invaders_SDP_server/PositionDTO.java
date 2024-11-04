package Invaders_SDP_server;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PositionDTO {

    private int playerX;
    private int playerY;
    private int enemyPlayerX;
    private int enemyPlayerY;


}
