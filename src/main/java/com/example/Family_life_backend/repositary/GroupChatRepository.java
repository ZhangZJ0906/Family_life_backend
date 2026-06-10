package com.example.Family_life_backend.repositary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.Family_life_backend.entity.GroupChatMessage;

public interface GroupChatRepository extends JpaRepository<GroupChatMessage, Long> {

	List<GroupChatMessage> findByGroupIdOrderByCreateTimeAsc(Long groupId);

	List<GroupChatMessage> findByGroupId(Long groupId);
}