package com.example.toychat.chatRoom.controller;

import com.example.toychat.chatRoom.dto.ChatRoomDTO;
import com.example.toychat.chatRoom.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatRoomController {
    private final ChatService chatService;

    // 이메일 꺼내기
    private String getMyEmail(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // 방 생성
    @PostMapping("/rooms")
    public ResponseEntity<ChatRoomDTO> createChatRoom(@RequestParam String name){ // Body가 아니므로 주소창에 보임
        return ResponseEntity.ok(chatService.createChatRoom(name, getMyEmail()));
    }

    // 내가 참여한 방 목록
    @GetMapping("/rooms/my")
    public ResponseEntity<List<ChatRoomDTO>> findMyChatRooms(){
        return ResponseEntity.ok(chatService.findMyRooms(getMyEmail()));
    }

    // 방 참여하기
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<String> joinRoom(@PathVariable String roomId){
        chatService.joinRoom(roomId, getMyEmail());
        return ResponseEntity.ok("참여 완료");
    }
}
