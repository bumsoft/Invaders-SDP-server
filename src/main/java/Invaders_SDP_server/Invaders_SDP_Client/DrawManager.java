package Invaders_SDP_server.Invaders_SDP_Client;

import Invaders_SDP_server.Bullet;
import Invaders_SDP_server.BulletPositionDTO;
import Invaders_SDP_server.Player;
import Invaders_SDP_server.PositionDTO;

import java.awt.*;

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

    public Player getPlayer() {
        return player;
    }

    public Player getEnemyPlayer() {
        return enemyPlayer;
    }

    // 플레이어 총알 동기화
    public void updatePlayerBullets(List<Bullet> bullets) {
        playerBullets.clear(); // 기존 총알 리스트 초기화
        playerBullets.addAll(bullets); // 새로 전달된 총알 리스트 추가
    }

    // 적 총알 동기화
    public void updateEnemyBullets(List<Bullet> bullets) {
        enemyBullets.clear(); // 기존 적 총알 리스트 초기화
        enemyBullets.addAll(bullets); // 새로 전달된 적 총알 리스트 추가
    }


    // player, enemy_player 위치 업데이트
    public void updatePositions(PositionDTO positionDTO) {
        player.setPosition(positionDTO.getPlayerX(), positionDTO.getPlayerY());
        enemyPlayer.setPosition(positionDTO.getEnemyPlayerX(), positionDTO.getEnemyPlayerY());
    }

    // 총알 리스트 업데이트
    public void updateBullets(List<BulletPositionDTO> playerBulletsDTO, List<BulletPositionDTO> enemyBulletsDTO) {
        // Null 체크 및 초기화
        if (playerBulletsDTO == null) playerBulletsDTO = new ArrayList<>();
        if (enemyBulletsDTO == null) enemyBulletsDTO = new ArrayList<>();

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
    public void draw(Graphics g) {
        // 플레이어 그리기
        g.setColor(Color.WHITE);
        g.fillRect(player.getX(), player.getY(), 50, 50); // 플레이어 렌더링

        // 적 플레이어 그리기
        g.setColor(Color.RED);
        g.fillRect(enemyPlayer.getX(), enemyPlayer.getY(), 50, 50); // 적 플레이어 렌더링

        // 플레이어 총알 그리기
        g.setColor(Color.YELLOW); // 총알 색상 변경
        for (Bullet bullet : playerBullets) {
            g.fillRect(bullet.getX(), bullet.getY(), 5, 10); // 총알 렌더링
            System.out.println("Drawing bullet at: (" + bullet.getX() + ", " + bullet.getY() + ")"); // 디버깅 로그
        }

        // 적 총알 그리기
        g.setColor(Color.RED);
        for (Bullet bullet : enemyBullets) {
            g.fillRect(bullet.getX(), bullet.getY(), 5, 10);
        }
    }

}
