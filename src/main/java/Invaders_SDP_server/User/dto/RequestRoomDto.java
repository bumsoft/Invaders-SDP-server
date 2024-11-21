package Invaders_SDP_server.User.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestRoomDto {

    private Long player1Id;

    private Long player2Id;

    private long key;
}
