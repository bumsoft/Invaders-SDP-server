package Invaders_SDP_server.User.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Long score = 0L;


    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    public void updateScore(Long score)
    {
        this.score = score;
    }
}
