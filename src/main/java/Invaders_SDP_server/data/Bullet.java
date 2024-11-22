package Invaders_SDP_server.data;

import lombok.Data;

@Data
// 서버에서 총알 관리 (Player 와는 독립적으로 작성)
public class Bullet {
    private static final int SCREEN_HEIGHT = 600;
    // 총알 위치 좌표, 속도, 방향
    private int x;
    private int y;

    public Bullet(int x, int y, boolean direction) {
        this.x = x;
        this.y = y;
    }

    // 총알과 상대 플레이어의 위치를 비교하여 충돌 여부 확인
    public boolean checkCollision(Player enemyPlayer) {
        int enemyPlayerX = enemyPlayer.getX();
        int enemyPlayerY = enemyPlayer.getY();

        // 총알과 플레이어의 위치 차이가 일정 거리 이내인지
        int collisionThreshold = 5; // 추후 함선의 크기에 맞게 조정
        return Math.abs(this.x - enemyPlayerX) < collisionThreshold &&
                Math.abs(this.y - enemyPlayerY) < collisionThreshold;
    }

    // 총알이 화면 밖으로 나갔는지 확인하는 메소드
    public boolean isOutOfBounds() {
        return y < 0 || y > SCREEN_HEIGHT; //
    }
}
