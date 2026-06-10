package com.example.Family_life_backend.repositary;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Family_life_backend.entity.GroupChatRead;

public interface GroupChatReadRepository extends JpaRepository<GroupChatRead, Long> {

	long countByMessageId(Long messageId);

	boolean existsByMessageIdAndUserId(Long messageId, Long userId);
}