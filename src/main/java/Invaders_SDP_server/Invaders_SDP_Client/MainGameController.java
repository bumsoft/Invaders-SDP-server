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
        String command = null;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_A -> command = pressed ? "a" : "stop";
            case KeyEvent.VK_W -> command = pressed ? "w" : "stop";
            case KeyEvent.VK_S -> command = pressed ? "s" : "stop";
            case KeyEvent.VK_D -> command = pressed ? "d" : "stop";
            case KeyEvent.VK_SPACE -> command = pressed ? "shoot" : "stopShoot";
        }

        if (command != null) {
            webSocketClientManager.sendMessage(command);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 배경 초기화
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // DrawManager를 통해 플레이어와 적, 총알 등을 렌더링
        drawManager.draw(g);
    }

    public void startGameLoop() {
        Timer timer = new Timer(16, e -> {
            updateGameState(); // 게임 상태 갱신
            repaint(); // 화면 갱신
        });
        timer.start();
    }

    private void updateGameState() {
        // PositionDTO: 두 플레이어의 위치 정보
        PositionDTO playerPosition = gameStateManager.getPlayerPosition(); // 서버에서 받은 플레이어 위치
        if (playerPosition == null) {
            playerPosition = new PositionDTO(200, 200, 100, 100); //
        }

        // List<BulletPositionDTO>: 플레이어와 적 총알 리스트
        List<BulletPositionDTO> playerBullets = gameStateManager.getPlayerBullets();
        List<BulletPositionDTO> enemyBullets = gameStateManager.getEnemyBullets();

        // Null 방지: 리스트가 null이면 빈 리스트로 초기화
        if (playerBullets == null) playerBullets = new ArrayList<>();
        if (enemyBullets == null) enemyBullets = new ArrayList<>();

        // GameStateDTO 생성
        GameStateDTO gameState = new GameStateDTO(playerPosition, playerBullets, enemyBullets);

        // GameStateManager 업데이트
        gameStateManager.updateGameState(gameState);

        // DrawManager를 사용하여 위치 및 총알 업데이트
        drawManager.updatePositions(gameStateManager.getPlayerPosition());
        drawManager.updateBullets(gameStateManager.getPlayerBullets(), gameStateManager.getEnemyBullets());
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

