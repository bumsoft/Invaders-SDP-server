package Invaders_SDP_server.service;

import Invaders_SDP_server.entity.User;
import Invaders_SDP_server.entity.Room;
import Invaders_SDP_server.repository.RoomRepository;
import Invaders_SDP_server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Room newRoom = Room.builder().player1(player1.get()).accessCode((long)(Math.random()*8999)+1000).player1Ready(false).player2Ready(false).build();
        Room created = roomRepository.save(newRoom);
        log.info("방생성완료 : {}",created);
        return created;
    }

    @Transactional
    public Room joinRoom(String player2Name, long joinKey){
        Optional<User> player2 = userRepository.findByUsername(player2Name);
        Room target = roomRepository.findByAccessCode(joinKey);
        if(target.getPlayer2() == null){
            Room updateRoom = Room.builder().id(target.getId()).player1(target.getPlayer1()).player2(player2.get()).player1Ready(false).player2Ready(false).accessCode(target.getAccessCode()).build();
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
            Room updateRoom = Room.builder().id(target.get().getId()).player1(target.get().getPlayer1()).player2(target.get().getPlayer2()).player1Ready(target.get().isPlayer1Ready()).player2Ready(true).accessCode(target.get().getAccessCode()).build();
            Room updated = roomRepository.save(updateRoom);
            return updated;
        }
        else{
            Room updateRoom = Room.builder().id(target.get().getId()).player1(target.get().getPlayer1()).player2(target.get().getPlayer2()).player1Ready(true).player2Ready(target.get().isPlayer2Ready()).accessCode(target.get().getAccessCode()).build();
            Room updated = roomRepository.save(updateRoom);
            return updated;
        }
    }

    @Transactional
    public Room deleteRoom(String username){
        Optional<User> player = userRepository.findByUsername(username);
        Optional<Room> target = roomRepository.findByPlayer1(player.get());
        if(target.isEmpty()){
            target = roomRepository.findByPlayer2(player.get());
        }
        roomRepository.delete(target.get());
        return target.get();
    }

    @Transactional
    public RoomStatus getRoom(String username){
        Optional<User> player = userRepository.findByUsername(username);
        Optional<Room> target = roomRepository.findByPlayer1(player.get());
        if(target.isEmpty()){
            target = roomRepository.findByPlayer2(player.get());
            return new RoomStatus(2,target.get());
        }
        return new RoomStatus(1,target.get());
    }
}
