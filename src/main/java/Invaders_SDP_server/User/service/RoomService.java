package Invaders_SDP_server.User.service;

import Invaders_SDP_server.User.entity.User;
import Invaders_SDP_server.User.entity.Room;
import Invaders_SDP_server.User.repository.RoomRepository;
import Invaders_SDP_server.User.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public Room createRoom(String username){
        Optional<User> player1 = userRepository.findByUsername(username);
        Room newRoom = Room.builder().player1(player1.get()).key((long)(Math.random()*8999)+1000).player1Ready(false).player2Ready(false).build();
        Room created = roomRepository.save(newRoom);
        log.info("방생성완료 : {}",created);
        return created;
    }

    @Transactional
    public Room joinRoom(String player2Name, long joinKey){
        Optional<User> player2 = userRepository.findByUsername(player2Name);
        Room target = roomRepository.findByKey(joinKey);
        if(target.getPlayer2() == null){
            Room updateRoom = Room.builder().id(target.getId()).player1(target.getPlayer1()).player2(player2.get()).player1Ready(false).player2Ready(false).key(target.getKey()).build();
            Room updated = roomRepository.save(updateRoom);
            return updated;
        }
        return null;
    }

    @Transactional
    public Room playerReady(String username){
        Optional<User> player = userRepository.findByUsername(username);
        Optional<Room> target = roomRepository.findByPlayer1(player.get());
        if(target.isEmpty()){
            target = roomRepository.findByPlayer2(player.get());
            Room updateRoom = Room.builder().id(target.get().getId()).player1(target.get().getPlayer1()).player2(target.get().getPlayer2()).player1Ready(target.get().isPlayer1Ready()).player2Ready(true).key(target.get().getKey()).build();
            Room updated = roomRepository.save(updateRoom);
            return updated;
        }
        else{
            Room updateRoom = Room.builder().id(target.get().getId()).player1(target.get().getPlayer1()).player2(target.get().getPlayer2()).player1Ready(true).player2Ready(target.get().isPlayer2Ready()).key(target.get().getKey()).build();
            Room updated = roomRepository.save(updateRoom);
            return updated;
        }
    }
}
