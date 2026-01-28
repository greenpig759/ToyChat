package com.example.toychat.chatRoom;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne // 여러명의 멤버가 하나의 채팅방에 소속
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    private String email;

    @Builder
    public ChatRoomMember(ChatRoom chatRoom, String email) {
        this.chatRoom = chatRoom;
        this.email = email;
    }
}
