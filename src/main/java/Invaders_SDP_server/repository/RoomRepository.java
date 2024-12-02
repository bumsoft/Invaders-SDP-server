package Invaders_SDP_server.repository;

import Invaders_SDP_server.entity.Room;
import Invaders_SDP_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository <Room,Long> {

    Room findByAccessCode(long key);
    Optional<Room> findByPlayer1(User player1);
    Optional<Room> findByPlayer2(User player2);

    void removeById(Long roomId);
}
