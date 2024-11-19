package Invaders_SDP_server.Invaders_SDP_Client;

import Invaders_SDP_server.Bullet;
import Invaders_SDP_server.BulletPositionDTO;
import Invaders_SDP_server.Player;
import Invaders_SDP_server.PositionDTO;


import java.util.ArrayList;
import java.util.List;

// 객체 상태 관리, GameRenderer에 전달
public class DrawManager {
    // player, enemy, player bullet, enemyBullet
    private final Player player;
    private final Player enemyPlayer;
    private final List<Bullet> playerBullets = new ArrayList<>();
    private final List<Bullet> enemyBullets = new ArrayList<>();

    public DrawManager(Player player, Player enemyPlayer) {
        this.player = player;
        this.enemyPlayer = enemyPlayer;
    }

    // player, enemy_player 위치 업데이트
    public void updatePositions(PositionDTO positionDTO) {
        player.setPosition(positionDTO.getPlayerX(), positionDTO.getPlayerY());
        enemyPlayer.setPosition(positionDTO.getEnemyPlayerX(), positionDTO.getEnemyPlayerY());
    }

    // 총알 리스트 업데이트
    public void updateBullets(List<BulletPositionDTO> playerBulletsDTO, List<BulletPositionDTO> enemyBulletsDTO) {
        playerBullets.clear();
        for (BulletPositionDTO bulletDTO : playerBulletsDTO) {
            playerBullets.add(new Bullet(bulletDTO.getBulletX(), bulletDTO.getBulletY(), bulletDTO.isDirection()));
        }

        enemyBullets.clear();
        for (BulletPositionDTO bulletDTO : enemyBulletsDTO) {
            enemyBullets.add(new Bullet(bulletDTO.getBulletX(), bulletDTO.getBulletY(), bulletDTO.isDirection()));
        }
    }

    // 현재 화면에 표시할 상태를 GameRenderer에 전달
    public void draw(GameRenderer renderer) {
        renderer.drawPlayer(player.getX(), player.getY());
        renderer.drawEnemyPlayer(enemyPlayer.getX(), enemyPlayer.getY());

        for (Bullet bullet : playerBullets) {
            renderer.drawBullet(bullet.getX(), bullet.getY(), "green");
        }

        for (Bullet bullet : enemyBullets) {
            renderer.drawBullet(bullet.getX(), bullet.getY(), "purple");
        }
    }
}
