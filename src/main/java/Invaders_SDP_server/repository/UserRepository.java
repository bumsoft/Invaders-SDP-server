package Invaders_SDP_server.repository;

import Invaders_SDP_server.entity.User;
import Invaders_SDP_server.dto.Response_UserScoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT new Invaders_SDP_server.User.dto.Response_UserScoreDto(u.username, u.score) FROM User u ORDER BY u.score desc")
    List<Response_UserScoreDto> findAllUserScores();
}
