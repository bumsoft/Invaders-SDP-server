package Invaders_SDP_server;

import Invaders_SDP_server.entity.Room;
import Invaders_SDP_server.entity.User;
import Invaders_SDP_server.repository.RoomRepository;
import Invaders_SDP_server.repository.UserRepository;
import Invaders_SDP_server.service.RoomService;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
// mock 관련 설정 문제로 테스트 실행이 잘 안되는 거 같음
@ExtendWith(MockitoExtension.class) // MockitoExtension을 사용하여 자동으로 모킹 초기화
class RoomServiceTest {

    @InjectMocks
    private RoomService roomService; // 서비스 객체

    @Mock
    private RoomRepository roomRepository; // RoomRepository 모킹

    @Mock
    private UserRepository userRepository; // UserRepository 모킹

    @Mock
    private User player1;
    @Mock
    private User player2;

    //Logger log;

    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);

        // Mockito가 제대로 동작하는지 확인하기 위한 로그 추가
        //log.info("Initializing mocks...");
        
        // User 객체 생성
        player1 = new User();
        player1.setUsername("player1");

        player2 = new User();
        player2.setUsername("player2");

        // UserRepository mock 설정 (유저가 반환되도록 설정)
        when(userRepository.findByUsername(player1.getUsername())).thenReturn(Optional.ofNullable(player1));
        when(userRepository.findByUsername(player2.getUsername())).thenReturn(Optional.ofNullable(player2));

        //log.info("Mocks initialized.");
    }

    @Test
    void testCreateRoom() {
        // Room 생성 시나리오
        Room room = Room.builder()
                .accessCode(1234)
                .player1(player1)
                .player2(null)  // player2는 아직 없음
                .player1Ready(false)
                .player2Ready(false)
                .build();

        // Repository mock 설정 (save() 호출 시, 저장된 객체를 확인)
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> {
            Room savedRoom = invocation.getArgument(0); // save 시 전달된 Room 객체
            //log.info("Saving room: {}", savedRoom); // 로그로 출력
            return savedRoom; // 전달된 객체를 그대로 반환
        });

        // RoomService의 createRoom 메서드 호출
        Room createdRoom = roomService.createRoom(player1.getUsername());

        // 결과 확인
        assertNotNull(createdRoom);
        assertEquals(1234, createdRoom.getAccessCode());
        assertEquals("player1", createdRoom.getPlayer1().getUsername());
        assertNull(createdRoom.getPlayer2());  // 아직 player2는 없음

        // save() 메서드가 호출되었는지 확인
        verify(roomRepository, times(1)).save(any(Room.class));  // save가 정확히 한 번 호출되었는지 확인
    }

    @Test
    void testJoinRoom() {
        // 이미 생성된 Room 객체
        Room room = new Room(1L, 1234, player1, null, false, false);

        // RoomRepository mock 설정
        when(roomRepository.findByAccessCode(1234)).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(room); // 방 저장도 mock 설정

        // Player2가 방에 조인한다고 가정
        Room updatedRoom = roomService.joinRoom("player2", 1234);

        // Player2가 room에 추가되었는지 확인
        assertNotNull(updatedRoom.getPlayer2()); //
        assertEquals("player2", updatedRoom.getPlayer2().getUsername());
    }

    @Test
    void testReadyPlayer() {
        // Room 생성
        Room room = new Room(1L, 1234, player1, player2, false, false);

        // RoomRepository mock 설정
        when(roomRepository.findByAccessCode(1234)).thenReturn(room);

        // Player1이 준비 완료
        roomService.playerReady("player1");

        // Player1의 상태가 'Ready'로 변경되었는지 확인
        assertTrue(room.isPlayer1Ready());
        assertFalse(room.isPlayer2Ready());  // Player2는 아직 준비 안 됨
    }

    @Test
    void testPlayerReadyBoth() {
        // Room 생성
        Room room = new Room(1L, 1234, player1, player2, false, false);

        // RoomRepository mock 설정
        when(roomRepository.findByAccessCode(1234)).thenReturn(room);

        // 두 플레이어가 모두 준비 완료
        roomService.playerReady("player1");
        roomService.playerReady("player2");

        // 두 플레이어가 모두 준비 상태로 변경되었는지 확인
        assertTrue(room.isPlayer1Ready());
        assertTrue(room.isPlayer2Ready());
    }

    @Test
    void testJoinRoomWhenRoomFull() {
        // 이미 생성된 Room 객체
        Room room = new Room(1L, 1234, player1, player2, false, false);

        // RoomRepository mock 설정
        when(roomRepository.findByAccessCode(1234)).thenReturn(room);

        // Player2가 이미 있는 방에 다시 참가하려 할 때
        Room updatedRoom = roomService.joinRoom("player2", 1234);

        // 방이 가득 차서 참가할 수 없으므로 null이 반환되어야 함
        assertNull(updatedRoom);
    }
}
