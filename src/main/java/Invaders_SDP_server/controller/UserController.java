package Invaders_SDP_server.controller;

import Invaders_SDP_server.dto.Request_RegisterDto;
import Invaders_SDP_server.dto.Response_UserScoreDto;
import Invaders_SDP_server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

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
