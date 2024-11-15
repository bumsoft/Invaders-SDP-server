package Invaders_SDP_server.User.service;

import Invaders_SDP_server.User.UserRepository;
import Invaders_SDP_server.User.domain.User;
import Invaders_SDP_server.User.dto.RegisterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterDto registerDto)
    {
        User user = new User();
        //이미 유저가 존재하는지에 대한 예외처리 해주기
        //..

        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        return userRepository.save(user);
    }

}
