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

    private int player1X;
    private int player1Y;
    private List<BulletPositionDTO> player1BulletPositionDTO;

    private int player2X;
    private int player2Y;
    private List<BulletPositionDTO> player2BulletPositionDTO;
}
