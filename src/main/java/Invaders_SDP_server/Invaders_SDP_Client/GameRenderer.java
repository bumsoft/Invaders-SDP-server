package Invaders_SDP_server.Invaders_SDP_Client;

import java.awt.*;

// 화면 렌더링
public class GameRenderer {

    private final Canvas canvas;
    private final Graphics graphics;

    public GameRenderer(Canvas canvas, Graphics graphics) {
        this.canvas = canvas;
        this.graphics = graphics;
    }

    // 화면 초기화
    public void clearScreen(){
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // player 그리기
    public void drawPlayer(int x, int y){
        graphics.setColor(Color.BLUE);
        graphics.fillRect(x, y, 10, 10);
    }

    // enemyPlayer그리기
    public void drawEnemyPlayer(int x, int y){
        graphics.setColor(Color.RED);
        graphics.fillRect(x, y, 10, 10);
    }


    // 총알 그리기
    public void drawBullet(int x, int y, String color){
        // 문자열로 전달된 색상을 Color 객체로 변환
        switch (color.toLowerCase()) {
            case "green":
                graphics.setColor(Color.GREEN);
                break;
            case "purple":
                graphics.setColor(new Color(128, 0, 128)); // 보라색
                break;
            default:
                graphics.setColor(Color.WHITE); // 기본 색상 설정
                break;
        }
        graphics.fillRect(x, y, 4, 4);
    }

    // 배경 그리기
    public void drawBackground() {
        graphics.setColor(Color.BLACK); // 배경색 설정
        graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    // 그래픽 리소스 해제 (필수)
    public void dispose() {
        graphics.dispose();
    }

}
