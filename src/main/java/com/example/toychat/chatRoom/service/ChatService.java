package com.example.toychat.chatRoom.service;


import com.example.toychat.chatRoom.dto.ChatRoomDTO;
import com.example.toychat.chatRoom.entity.ChatRoom;
import com.example.toychat.chatRoom.entity.ChatRoomMember;
import com.example.toychat.chatRoom.repository.ChatRoomMemberRepository;
import com.example.toychat.chatRoom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    // 방 만들기
    public ChatRoomDTO createChatRoom(String name, String email) {
        ChatRoom chatRoom = ChatRoom.builder()
                .name(name)
                .ownerEmail(email)
                .build();

        // 채팅방의 정보를 저장
        chatRoomRepository.save(chatRoom);

        // 이때 방장을 멤버로 지정한다
        ChatRoomMember chatRoomMember = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .email(email)
                .build();

        // 멤버의 정보를 저장
        chatRoomMemberRepository.save(chatRoomMember);

        // 해당 채팅방의 DTO 정보를 넘기고 마무리
        return new ChatRoomDTO(chatRoom);
    }

    // 내가 참여한 채팅방 목록 가져오기
    public List<ChatRoomDTO> findMyRooms(String email){
        return chatRoomMemberRepository.findAllByEmail(email).stream() // 리스트 데이터를 처리
                .map(member -> new ChatRoomDTO(member.getChatRoom()))// ChatRoomDTO 형태로 매핑
                .collect(Collectors.toList()); // 처리를 완료한 정보들을 List 형태로 변환 후 반환
    }

    // 방 참여하기
    public void joinRoom(String roomId, String email){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("방을 찾을 수 없습니다"));

        // 중복 가입 방지
        if(chatRoomMemberRepository.existsByChatRoomAndEmail(chatRoom, email)){
            throw new IllegalArgumentException("이미 소속된 채팅방 입니다");
        }

        // 가입 처리 완료
        ChatRoomMember member = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .email(email)
                .build();
        chatRoomMemberRepository.save(member);
    }
}
