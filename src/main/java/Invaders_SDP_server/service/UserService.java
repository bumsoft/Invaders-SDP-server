package Invaders_SDP_server.service;

import Invaders_SDP_server.repository.UserRepository;
import Invaders_SDP_server.entity.User;
import Invaders_SDP_server.dto.Request_RegisterDto;
import Invaders_SDP_server.dto.Response_UserScoreDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    public User register(Request_RegisterDto requestRegisterDto)
    {
        //이미 유저가 존재하는지에 대한 예외처리 해주기
        User user = new User(requestRegisterDto.getUsername(),passwordEncoder.encode(requestRegisterDto.getPassword()));
        return userRepository.save(user);
    }

    public List<Response_UserScoreDto> getRanking()
    {
        return userRepository.findAllUserScores();
    }

    public void updateRank(String username, Long score)
    {
        Optional<User> _user = userRepository.findByUsername(username);
        if(_user.isPresent())
        {
            User user = _user.get();
            if(user.getScore() < score)
            {
                user.updateScore(score);
                userRepository.save(user);
            }
        }
    }
}
