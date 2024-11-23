package Invaders_SDP_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class BulletPositionDTO {

    private int bX; // bulletX
    private int bY; // bulletY
}
