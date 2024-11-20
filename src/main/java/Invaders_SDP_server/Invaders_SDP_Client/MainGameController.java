package Invaders_SDP_server.Invaders_SDP_Client;

import Invaders_SDP_server.BulletPositionDTO;
import Invaders_SDP_server.GameStateDTO;
import Invaders_SDP_server.Player;
import Invaders_SDP_server.PositionDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainGameController extends JPanel {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private GameStateManager gameStateManager;
    private WebSocketClientManager webSocketClientManager;
    private DrawManager drawManager;

    private final boolean[] keyStates = new boolean[256];

    public MainGameController() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK); // 배경 설정

        // 게임 상태 및 웹소켓 초기화
        gameStateManager = new GameStateManager();
        webSocketClientManager = new WebSocketClientManager("ws://localhost:8080/game", gameStateManager);

        // 플레이어 초기화
        Player player = new Player(400, 500, true, new ArrayList<>(), false, false); // 플레이어 초기 위치
        Player enemyPlayer = new Player(400, 100, false, new ArrayList<>(), false, false); // 적 초기 위치

        // DrawManager 초기화
        drawManager = new DrawManager(player, enemyPlayer);

        // 키 입력 처리
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyInput(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                handleKeyInput(e, false);
            }
        });
        setFocusable(true); // 키 입력 활성화
    }

    private void handleKeyInput(KeyEvent e, boolean pressed) {
        int key = e.getKeyCode();
        if (key < keyStates.length) {
            keyStates[key] = pressed; // 키 상태 기록
        }

        // 키 상태에 따라 움직임 처리
        if (keyStates[KeyEvent.VK_A]) {
            drawManager.getPlayer().move("a");
        }
        if (keyStates[KeyEvent.VK_D]) {
            drawManager.getPlayer().move("d");
        }
        if (keyStates[KeyEvent.VK_W]) {
            drawManager.getPlayer().move("w");
        }
        if (keyStates[KeyEvent.VK_S]) {
            drawManager.getPlayer().move("s");
        }

        if (!pressed) {
            drawManager.getPlayer().stopMoving(); // 키를 뗐을 때 멈춤 처리
        }

        repaint(); // 화면 갱신
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경 초기화
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // DrawManager 통해 플레이어와 적, 총알 등을 렌더링
        drawManager.draw(g);
    }

    public void startGameLoop() {
        Timer timer = new Timer(16, e -> {
            updateGameState(); // 게임 상태 갱신
            repaint(); // 화면 갱신
        });
        timer.start();
    }

    public void updateGameState() {ad
        // 클라이언트 상태를 기준으로 서버와 동기화
        PositionDTO clientPosition = new PositionDTO(
                drawManager.getPlayer().getX(), // 플레이어 X 좌표
                drawManager.getPlayer().getY(), // 플레이어 Y 좌표
                drawManager.getEnemyPlayer().getX(), // 적 플레이어 X 좌표
                drawManager.getEnemyPlayer().getY() // 적 플레이어 Y 좌표
        );

        // 클라이언트의 현재 상태를 기반으로 GameStateDTO 생성 및 업데이트
        gameStateManager.updateGameState(new GameStateDTO(
                clientPosition, // 현재 위치 정보
                gameStateManager.getPlayerBullets(), // 플레이어 총알 정보
                gameStateManager.getEnemyBullets() // 적 총알 정보
        ));

        // DrawManager를 사용하여 위치 및 총알 상태 업데이트
        drawManager.updatePositions(gameStateManager.getPlayerPosition());
        drawManager.updateBullets(
                gameStateManager.getPlayerBullets(),
                gameStateManager.getEnemyBullets()
        );
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Invaders Game");
        MainGameController gamePanel = new MainGameController();

        frame.add(gamePanel);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gamePanel.startGameLoop(); // 게임 루프 시작
    }
}

