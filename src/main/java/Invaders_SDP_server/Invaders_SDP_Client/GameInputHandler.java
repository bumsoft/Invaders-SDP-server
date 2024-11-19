package Invaders_SDP_server.Invaders_SDP_Client;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// To do: Key 입력 감지 , 이벤트 처리, 서버 통신
// 클라이언트 입력 처리
// spacebar -> bulletShoot
// 이동 -> a, w, s, d
public class GameInputHandler implements KeyListener {
    private final WebSocketClientManager webSocketClientManager;

    public GameInputHandler(WebSocketClientManager clientManager) {
        this.webSocketClientManager = clientManager;
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_A -> webSocketClientManager.sendMessage("a"); // 왼쪽 이동
            case KeyEvent.VK_W -> webSocketClientManager.sendMessage("w"); // 위로 이동
            case KeyEvent.VK_S -> webSocketClientManager.sendMessage("s"); // 아래로 이동
            case KeyEvent.VK_D -> webSocketClientManager.sendMessage("d"); // 오른쪽 이동
            case KeyEvent.VK_SPACE -> webSocketClientManager.sendMessage("shoot"); // 총알 발사
        }
    }

    @Override
    public void keyReleased(KeyEvent event) {
        // 키를 뗐을 때 동작 처리
        switch (event.getKeyCode()) {
            case KeyEvent.VK_A, KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_D ->
                    webSocketClientManager.sendMessage("stop"); // 움직임 멈춤
            case KeyEvent.VK_SPACE ->
                    webSocketClientManager.sendMessage("stopShoot"); // 총알 발사 멈춤
        }
    }

    @Override
    public void keyTyped(KeyEvent event) {

    }

}
