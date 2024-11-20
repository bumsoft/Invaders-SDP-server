package Invaders_SDP_server.User.controller;

import Invaders_SDP_server.User.dto.Request_RegisterDto;
import Invaders_SDP_server.User.dto.Response_UserScoreDto;
import Invaders_SDP_server.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody Request_RegisterDto requestRegisterDto) {
        try {
            userService.register(requestRegisterDto);
            return ResponseEntity.ok().build();
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/username")
    public String findUsername(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/ranking")
    public List<Response_UserScoreDto> ranking()
    {
        return userService.getRanking();
    }

    @GetMapping("/updateRank/{score}")
    public void updateRank(@PathVariable("score") Long score,Principal principal)
    {
        userService.updateRank(principal.getName(),score);
    }
}
