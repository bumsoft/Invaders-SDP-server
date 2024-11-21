package Invaders_SDP_server.User.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@AllArgsConstructor
public class BulletPositionDTO {

    private int bulletX;
    private int bulletY;
    private boolean direction;
}
