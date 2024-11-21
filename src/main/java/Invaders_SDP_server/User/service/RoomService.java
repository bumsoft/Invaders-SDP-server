package Invaders_SDP_server.User.service;

import Invaders_SDP_server.User.domain.User;
import Invaders_SDP_server.User.entity.Room;
import Invaders_SDP_server.User.repository.RoomRepository;
import Invaders_SDP_server.User.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public Room createRoom(Long player1Id){

        User player1 = userRepository.findById(player1Id).orElse(null);
        if(player1 == null){
            throw new IllegalArgumentException("player1 찾기 실패!");
        }
        Room newRoom = Room.builder().player1(player1).key((long)(Math.random()*8999)+1000).build();
        Room created = roomRepository.save(newRoom);
        log.info("방생성완료 : {}",created);
        return created;
    }

    @Transactional
    public Room joinRoom(Long player2Id, long joinKey){
        User player2 = userRepository.findById(player2Id).orElse(null);
        if(player2 ==null){
            throw new IllegalArgumentException("player2 찾기 실패!");
        }
        Room target = roomRepository.findByKey(joinKey);
        if(target == null){
            throw new IllegalArgumentException("방찾기 실패!");
        }
        else if(target.getPlayer2() == null){
            throw new IllegalArgumentException("플레이어가 이미 있습니다!");
        }
        Room updateRoom = Room.builder().id(target.getId()).player1(target.getPlayer1()).player2(player2).key(target.getKey()).build();
        Room updated = roomRepository.save(updateRoom);
        log.info("방입장완료 : {}",updated);
        return updated;
    }
}
