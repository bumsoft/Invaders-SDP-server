package Invaders_SDP_server.User.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UserScoreDto {
    private String username;
    private Long score;
}
