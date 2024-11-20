package Invaders_SDP_server.Invaders_SDP_Client;

import Invaders_SDP_server.BulletPositionDTO;
import Invaders_SDP_server.GameStateDTO;
import Invaders_SDP_server.PositionDTO;
import lombok.Getter;

import java.util.List;

// 게임 상태 관리
@Getter
public class GameStateManager {

    // 상태 가져오기
    private PositionDTO playerPosition;
    private List<BulletPositionDTO> playerBullets;
    private List<BulletPositionDTO> enemyBullets;

    public void updateGameState(GameStateDTO gameState) {
        this.playerPosition = gameState.getPositionDTO();
        this.playerBullets = gameState.getPlayerBulletPositionDTO();
        this.enemyBullets = gameState.getEnemyBulletPositionDTO();
    }


}
