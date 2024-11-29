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

    // Room 클래스에서 id는 @Id로 지정된 필드이며,
    // @GeneratedValue 전략에 의해 자동으로 생성되는 값이기 때문에, setID 메서드를 사용할 수 없습니다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_number")
    private Long id;

    private long accessCode;

    @OneToOne
    @JoinColumn
    private User player1;

    @OneToOne
    @JoinColumn
    private User player2;

    private boolean player1Ready;

    private boolean player2Ready;

    // id 값을 수동으로 설정할 수 있는 setId 메서드 추가
    public void setId(Long id) {
        this.id = id;
    }

}

