package Invaders_SDP_server.Invaders_SDP_Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;

// 클라이언트 초기화 및 실행
public class MainGameController {
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    public static void main(String[] args) {
        // JFrame 생성 - GUI 생성
        JFrame frame = new JFrame("Invaders Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);

        // 캔버스 생성 및 추가 - 검은 배경 화면
        Canvas canvas = new Canvas();
        canvas.setSize(WIDTH, HEIGHT);
        canvas.setBackground(Color.BLACK);

        // WebSocketClientManager 및 GameInputHandler 초기화 - gameStateManager는 현재 null -> 비활성화 상태
        GameStateManager gameStateManager = new GameStateManager();
        WebSocketClientManager webSocketClientManager = new WebSocketClientManager("localhost:8080/game", gameStateManager);
        GameInputHandler inputHandler = new GameInputHandler(webSocketClientManager);

        // 입력 처리기 추가
        canvas.addKeyListener((KeyListener) inputHandler);
        canvas.setFocusable(true); // 키보드 포커스를 받을 수 있도록 설정

        // 캔버스를 프레임에 추가
        frame.add(canvas);
        frame.setVisible(true);

        // 간단한 렌더링 루프 - 수정 필요
        Graphics graphics = canvas.getGraphics();
        while (true) {
            // 화면을 검은색으로 초기화
            graphics.setColor(Color.BLACK);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);

            // 필요 시, 게임 상태에 따라 플레이어 및 기타 요소 그리기

            try {
                Thread.sleep(16); // 약 60FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
