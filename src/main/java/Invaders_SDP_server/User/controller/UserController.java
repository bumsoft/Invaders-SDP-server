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
        System.out.println("dd");
        try {
            userService.register(registerDto);
            System.out.println("가입됨: " + registerDto.getUsername());
            return ResponseEntity.ok().build();
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Void> test(Principal principal) {

        System.out.println(principal.getName());
        return ResponseEntity.ok().build();
    }
}
