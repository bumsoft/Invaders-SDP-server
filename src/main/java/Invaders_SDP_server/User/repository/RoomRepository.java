package Invaders_SDP_server.User.repository;

import Invaders_SDP_server.User.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository <Room,Long> {

    Room findByKey(long key);
}
