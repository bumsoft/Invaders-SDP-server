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
    public Room createRoom(String username)
    {
        Optional<User> player1 = userRepository.findByUsername(username);
        if (player1.isPresent())
        {
            Room newRoom = Room.builder()
                    .player1(player1.get())
                    .accessCode((long) (Math.random() * 8999) + 1000)
                    .player1Ready(false)
                    .player2Ready(false)
                    .build();
            Room created = roomRepository.save(newRoom);
            log.info("방생성완료 : {}", created);
            return created;
        } else
        {
            log.info("cannot find user:{} while creating room", username);
            return null;
        }
    }

    /**
     *
     * @return P2가 참가한 상태의 Room or null if 이미 누가 참가한 방이거나, 참가하는 유저정보가 없을 시.
     */
    @Transactional
    public Room joinRoom(String player2Name, long joinKey)
    {
        Optional<User> player2 = userRepository.findByUsername(player2Name);
        if (player2.isPresent())
        {
            Room target = roomRepository.findByAccessCode(joinKey);
            if (target.getPlayer2() == null)
            {
                Room updateRoom = Room.builder().
                        id(target.getId())
                        .player1(target.getPlayer1())
                        .player2(player2.get())
                        .player1Ready(false).player2Ready(false)
                        .accessCode(target.getAccessCode())
                        .build();
                Room updated = roomRepository.save(updateRoom);
                log.info("Access code:{}로 {}이 참가함", joinKey, player2Name);
                return updated;
            } else //이미 누가 참가한 방인 경우
            {
                log.info("Already Full room; Access denied:{}", player2Name);
                return null;
            }
        } else
        {
            log.info("cannot find user:{} while joining room", player2Name);
            return null; //p2가 없는경우
        }
    }

    @Transactional
    public Room playerReady(String username)
    {
        Optional<User> player = userRepository.findByUsername(username);
        if (player.isEmpty())
        {
            log.info("cannot find user:{} while Ready", username);
            return null;
        }
        Optional<Room> target = roomRepository.findByPlayer1(player.get());
        if (target.isPresent())
        {
            Room updateRoom = Room.builder()
                    .id(target.get().getId())
                    .player1(target.get().getPlayer1())
                    .player2(target.get().getPlayer2())
                    .player1Ready(true)
                    .player2Ready(target.get().isPlayer2Ready())
                    .accessCode(target.get().getAccessCode())
                    .build();
            log.info("{} ready", username);
            return roomRepository.save(updateRoom);
        }
        target = roomRepository.findByPlayer2(player.get());
        if (target.isPresent())
        {
            Room updateRoom = Room.builder()
                    .id(target.get().getId())
                    .player1(target.get().getPlayer1())
                    .player2(target.get().getPlayer2())
                    .player1Ready(target.get().isPlayer1Ready())
                    .player2Ready(true)
                    .accessCode(target.get().getAccessCode())
                    .build();
            log.info("{} ready", username);
            return roomRepository.save(updateRoom);
        }
        return null;
    }

    @Transactional
    public void deleteRoom(String username)
    {
        Optional<Room> target = getRoom(username);
        if (target.isPresent())
            roomRepository.delete(target.get());
        else
        {
            log.info("cannot find room:{} while deleting room", username);
        }
    }

    @Transactional
    public Optional<Room> getRoom(String username)
    {
        Optional<User> player = userRepository.findByUsername(username);
        if (player.isPresent())
        {
            Optional<Room> target = roomRepository.findByPlayer1(player.get());
            if (target.isPresent())
            {
                return target;
            } else
            {
                return roomRepository.findByPlayer2(player.get());
            }
        }
        else
        {
            log.info("cannot find user:{} while getting room", username);
            return Optional.empty();
        }
    }
}
