package Invaders_SDP_server.User.repository;

import Invaders_SDP_server.User.domain.User;
import Invaders_SDP_server.User.dto.UserScoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    @Query("SELECT new Invaders_SDP_server.User.dto.UserScoreDto(u.username, u.score) FROM User u ORDER BY u.score desc")
    List<UserScoreDto> findAllUserScores();
}
