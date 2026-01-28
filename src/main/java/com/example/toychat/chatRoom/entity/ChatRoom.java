package com.example.toychat.chatRoom;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
public class ChatRoom {

    @Id
    private String Id;

    @Column(nullable = false)
    private String name; // 방 이름

    @Column(nullable = false)
    private String ownerEmail; // 방장 이메일

    @Builder
    public ChatRoom(String name, String ownerEmail) {
        this.Id = UUID.randomUUID().toString(); // 방의 기본 키는 랜덤 Id를 생성하는 방식
        this.name = name;
        this.ownerEmail = ownerEmail;
    }
}
