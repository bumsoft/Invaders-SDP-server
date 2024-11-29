package Invaders_SDP_server;

import Invaders_SDP_server.data.Bullet;
import Invaders_SDP_server.data.Player;
import Invaders_SDP_server.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private GameService gameService;
    private Player player;
    private Player enemyPlayer;


    @BeforeEach
    void setUp() {
        gameService = new GameService();
        player = new Player();
        player.setX(100);
        player.setY(100);
        player.setWidth(30);  // 플레이어 크기
        player.setHeight(30);
        player.setDirection(true); // 위쪽으로 총알 발사

        // enemyPlayer 객체 초기화
        enemyPlayer = new Player();
        enemyPlayer.setX(100);
        enemyPlayer.setY(80); // 적 플레이어는 player 바로 아래
        enemyPlayer.setWidth(10);
        enemyPlayer.setHeight(10);
        enemyPlayer.setDirection(false); // 적은 아래쪽으로 총알 발사

        // 플레이어 총알 발사
        player.shoot_Bullet();

        // 적 플레이어 총알 발사
        enemyPlayer.shoot_Bullet();
    }

    @Test
    void testMovePlayerWithinBounds() {
        // 초기 위치 설정
        player.setX(90);
        assertEquals(90, player.getX());  // 초기 값 확인

        // 이동 테스트
        gameService.movePlayer(player, "right");
        assertEquals(97, player.getX());  // 오른쪽으로 7만큼 이동

        // 화면 경계를 넘어가는 경우 예외 처리
        player.setX(590);  // 오른쪽 경계 근처
        gameService.movePlayer(player, "right");
        assertEquals(600 - 42, player.getX());  // 화면 끝으로 이동

        gameService.movePlayer(player, "left");
        assertEquals(600 - 42 - 7, player.getX());  // 왼쪽으로 이동
    }

    @Test
    void testMoveBullets() {
        // 플레이어의 총알 이동 확인
        List<Bullet> bullets = player.getBullets();
        Bullet bullet = bullets.get(0);

        // 초기 총알 위치 확인
        assertEquals(100, bullet.getY());

        // 총알 이동 (위로 20만큼 이동)
        gameService.moveBullets(player);
        assertEquals(80, bullet.getY());  // Y 좌표가 80이어야 함

        // 적의 총알 이동 확인
        List<Bullet> enemyBullets = enemyPlayer.getBullets();
        Bullet enemyBullet = enemyBullets.get(0);

        // 초기 총알 위치 확인
        assertEquals(80, enemyBullet.getY());

        // 총알 이동 (아래로 20만큼 이동)
        gameService.moveBullets(enemyPlayer);
        assertEquals(100, enemyBullet.getY());  // Y 좌표가 100이어야 함
    }

    @Test
    void testCheckCollisionNoCollision() {
        // 충돌이 일어나지 않도록 설정
        player.setX(100);
        player.setY(100);
        enemyPlayer.setX(150);  // 적과의 x 좌표를 멀리 설정

        player.shoot_Bullet();  // 플레이어 총알 발사
        List<Bullet> enemyBullets = enemyPlayer.getBullets();

        // 충돌 여부 확인
        boolean collisionDetected = gameService.checkCollision(player, enemyBullets);
        assertFalse(collisionDetected);  // 충돌이 발생하지 않아야 함
    }

    /*@Test
    void testCheckCollisionWithCollision() {
        // 플레이어와 적의 총알 충돌이 발생하는 경우
        Bullet bullet = new Bullet();
        bullet.setX(player.getX() + player.getWidth() / 2);  // 플레이어의 중간 위치
        bullet.setY(player.getY() + player.getHeight() / 2); // 플레이어의 중간 위치

        enemyPlayer.shoot_Bullet();  // 적 플레이어가 총알을 발사한다고 가정
        boolean collisionDetected = gameService.checkCollision(player, enemyPlayer.getBullets());

        assertTrue(collisionDetected);  // 충돌이 발생해야 함
    } */

    @Test
    void testCheckCollisionWithoutCollision() {
        // 플레이어 총알 발사
        List<Bullet> bullets = player.getBullets();
        Bullet bullet = bullets.get(0);

        // 적 플레이어 총알 발사
        List<Bullet> enemyBullets = enemyPlayer.getBullets();
        Bullet enemyBullet = enemyBullets.get(0);

        // 플레이어 총알의 위치를 적과 충분히 떨어지게 설정
        bullet.setX(enemyPlayer.getX() + enemyPlayer.getWidth() + 10);  // 적과 충분히 떨어지게 위치
        bullet.setY(enemyPlayer.getY() + enemyPlayer.getHeight() + 10); // Y축도 충분히 떨어지게 설정

        // 적 플레이어의 총알이 플레이어와 충돌하지 않는지 확인
        boolean collisionDetected = gameService.checkCollision(player, enemyBullets);
        System.out.println("Collision detected: " + collisionDetected); // 충돌 여부 출력

        // 충돌이 발생하지 않아야 하므로 false를 반환해야 한다
        assertFalse(collisionDetected);  // 충돌이 발생하지 않아야 함
    }

    @Test
    void testHandleGameOver() {
        // 게임 오버 처리 확인
        player.setX(100);
        player.setY(100);
        enemyPlayer.setX(100); // 적과 같은 x 좌표
        enemyPlayer.setY(80);  // 적이 player 바로 아래에 위치

        player.shoot_Bullet(); // 플레이어 총알 발사
        List<Bullet> enemyBullets = enemyPlayer.getBullets();

        // 충돌 시 게임 오버 처리
        boolean collisionDetected = gameService.checkCollision(player, enemyBullets);

        if (collisionDetected) {
            gameService.handleGameOver(player);  // 게임 오버 처리
            System.out.println("Game Over for player: " + player.getUsername());
        }

        // 실제 게임 오버 처리는 출력 확인을 통해 체크
        // 이 테스트는 출력 로그를 통해 확인하거나, handleGameOver 메서드에서 다른 처리가 추가될 경우 이를 검증할 수 있음
    }

}
