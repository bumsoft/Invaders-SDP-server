package Invaders_SDP_server.User.controller;

import Invaders_SDP_server.User.dto.RegisterDto;
import Invaders_SDP_server.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterDto registerDto) {
        try {
            userService.register(registerDto);
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
}
