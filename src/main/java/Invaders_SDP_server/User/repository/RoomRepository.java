package Invaders_SDP_server.User.repository;

import Invaders_SDP_server.User.entity.Room;
import Invaders_SDP_server.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository <Room,Long> {

    Room findByKey(long key);
    Optional<Room> findByPlayer1(User player1);
    Optional<Room> findByPlayer2(User player2);
}
