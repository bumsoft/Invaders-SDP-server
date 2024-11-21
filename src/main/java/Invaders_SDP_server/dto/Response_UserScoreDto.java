package Invaders_SDP_server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Response_UserScoreDto {
    private String username;
    private Long score;
}
