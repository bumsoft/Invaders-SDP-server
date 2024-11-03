package Invaders_SDP_server;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    // 플레이어의 위치 좌표 x, y
    private int x;
    private int y;
}
