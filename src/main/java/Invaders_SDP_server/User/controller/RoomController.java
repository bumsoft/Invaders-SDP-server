package Invaders_SDP_server.User.controller;

import Invaders_SDP_server.User.dto.RequestRoomDto;
import Invaders_SDP_server.User.entity.Room;
import Invaders_SDP_server.User.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {

    @Autowired
    RoomService roomService;

    @PostMapping("/room/create")
    public ResponseEntity<Room> createRoom(@RequestBody RequestRoomDto requestRoomDto){
        Room created = roomService.createRoom(requestRoomDto.getPlayer1Id());
        if(created == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(created);
    }

    @GetMapping("/room/join/{key}")
    public ResponseEntity<Room> joinRoom(@RequestBody RequestRoomDto requestRoomDto){
        Room joined = roomService.joinRoom(requestRoomDto.getPlayer2Id(), requestRoomDto.getKey());
        if(joined == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(joined);
    }

}
