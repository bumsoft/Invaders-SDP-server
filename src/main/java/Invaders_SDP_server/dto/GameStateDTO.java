package Invaders_SDP_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GameStateDTO {

    private PositionDTO positionDTO;  // 두 플레이어의 위치 정보 담는다
    private List<BulletPositionDTO> playerBulletPositionDTO; // 플레이어의 총알 위치 담는다
    private List<BulletPositionDTO> enemyBulletPositionDTO; // 적의 총알 위치 담는다
}
