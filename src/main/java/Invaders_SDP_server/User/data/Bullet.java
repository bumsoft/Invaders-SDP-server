package Invaders_SDP_server.User.data;

import lombok.Data;

@Data
// 서버에서 총알 관리 (Player 와는 독립적으로 작성)
public class Bullet {
    private static final int SCREEN_HEIGHT = 600;
    // 총알 위치 좌표, 속도, 방향
    private int x;
    private int y;
    private boolean direction;  // True면 위로 발사, False면 아래로 발사
    static final int speed = 7; // 총알 속도 통일, 현재로서는 변경하지 않기로 함 - 수치는 추후 플레이 해보면서 조정 필요함

    public Bullet(int x, int y, boolean direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    // 총알 위치 업데이트 메소드
    public void updateBulletMove() {
        if(!direction) { // session1(player1)은 아래에서 위로만 공격
            y -= speed;
        }
        else{  //session2(player2)는 위에서 아래로만 공격
            y += speed;
        }
        System.out.println("Bullet position updated to: (" + x + ", " + y + ")"); // 디버깅 로그
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
