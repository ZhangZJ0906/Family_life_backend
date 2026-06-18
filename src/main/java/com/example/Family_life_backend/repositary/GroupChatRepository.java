package com.example.Family_life_backend.repositary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Family_life_backend.DTO.GroupChatMessageDto;
import com.example.Family_life_backend.entity.GroupChatMessage;

public interface GroupChatRepository extends JpaRepository<GroupChatMessage, Long> {

	List<GroupChatMessage> findByGroupIdOrderByCreateTimeAsc(Long groupId);

	List<GroupChatMessage> findByGroupId(Long groupId);

//	@Query("""
//			    SELECT new com.example.Family_life_backend.dto.GroupChatMessageDto(
//			        m.id,
//			        m.groupId,
//			        m.senderId,
//			        u.name,
//			        u.avatar,
//			        m.message,
//			        m.createTime,
//			        m.imageUrl,
//			        m.isRecall,
//			        m.replyId
//			    )
//			    FROM GroupChatMessage m
//			    LEFT JOIN UserInfo u ON u.userId = m.senderId
//			    WHERE m.groupId = :groupId
//			    ORDER BY m.createTime ASC
//			""")
//	List<GroupChatMessageDto> findChatMessagesWithUserInfo(@Param("groupId") Long groupId);
}