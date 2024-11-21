package Invaders_SDP_server.entity;

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

    @OneToOne
    @JoinColumn
    private User player1;

    @OneToOne
    @JoinColumn
    private User player2;

    private boolean player1Ready;

    private boolean player2Ready;


}

