package Invaders_SDP_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GameStateDTO {

    private int p1X; // player1X
    private int p1Y; // player1Y
    private List<BulletPositionDTO> p1b;

    private int p2X;
    private int p2Y;
    private List<BulletPositionDTO> p2b;
}
