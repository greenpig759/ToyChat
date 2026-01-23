package com.example.toychat.common.entity;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass   // 실제 Entity가 아닌 매핑정보만 자식에게 물려줌
@EntityListeners(AuditingEntityListener.class)   // JPA가 제공하는 기능으로 엔티티가 저장, 수정되는 순간을 파악 후 시간을 자동으로 넣어줌
public class BaseTimeEntity {
    @CreatedDate
    private LocalDateTime createdAt; // 생성 시간(INSERT)

    @LastModifiedDate
    private LocalDateTime updatedAt; // 수정 시간(UPDATE)
}
