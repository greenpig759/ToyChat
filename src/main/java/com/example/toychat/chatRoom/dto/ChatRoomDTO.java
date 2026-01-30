package com.example.toychat.chatRoom.dto;


import com.example.toychat.chatRoom.entity.ChatRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatRoomDTO {
    private String roomId;
    private String name;
    private String ownerEmail;

    // Entity에서 정보를 가져오는 구조의 생성자
    public ChatRoomDTO(ChatRoom chatRoom) {
        this.roomId = chatRoom.getId();
        this.name = chatRoom.getName();
        this.ownerEmail = chatRoom.getOwnerEmail();
    }
}
