package Invaders_SDP_server.User.entity;

import Invaders_SDP_server.User.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_number")
    private Long id;

    private long key;

    private User player1;

    private User player2;


}

