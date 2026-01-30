package com.example.toychat.chatRoom.repository;

import com.example.toychat.chatRoom.entity.ChatRoom;
import com.example.toychat.chatRoom.entity.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    // 중복 가입 방지용 확인 -> 해당 방에 해당 이메일이 있는지 확인
    boolean existsByChatRoomAndEmail(ChatRoom chatRoom, String email);

    // 내가 가입한 방 목록 확인하기
    List<ChatRoomMember> findAllByEmail(String email);
}
