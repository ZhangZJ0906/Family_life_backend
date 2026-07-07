package com.example.Family_life_backend.repositary;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import com.example.Family_life_backend.DTO.GroupChatMessageDto;
import com.example.Family_life_backend.entity.GroupChatMessage;

public interface GroupChatRepository extends JpaRepository<GroupChatMessage, Long> {

	// 查詢某群組最新一頁聊天訊息。
	// beforeId 為 null：查最新訊息。
	// beforeId 有值：查 id 小於 beforeId 的更舊訊息。
	// ORDER BY m.id DESC 是為了先從資料庫拿最新 N 筆，效能比全撈再切好。
	@Query("""
	        SELECT m
	        FROM GroupChatMessage m
	        WHERE m.groupId = :groupId
	          AND (:beforeId IS NULL OR m.id < :beforeId)
	        ORDER BY m.id DESC
	        """)
	List<GroupChatMessage> findPageBeforeId(
	        @Param("groupId") Long groupId,
	        @Param("beforeId") Long beforeId,
	        Pageable pageable
	);

	// 查詢某使用者在某群組中尚未讀的訊息。
	// senderId <> userId：自己發的訊息不需要標記已讀。
	// upToMessageId：只標記使用者目前看到的範圍，避免一次掃整個聊天室。
	// NOT EXISTS：排除 group_chat_read 已經有紀錄的訊息。
			@Query("""
			SELECT m
			FROM GroupChatMessage m
			WHERE m.groupId = :groupId
			AND m.senderId <> :userId
			AND (:upToMessageId IS NULL OR m.id <= :upToMessageId)
			AND NOT EXISTS (
				SELECT r.id
				FROM GroupChatRead r
				WHERE r.messageId = m.id
					AND r.userId = :userId
			)
			ORDER BY m.id ASC
		""")
		List<GroupChatMessage> findUnreadMessagesForUser(
				@Param("groupId") Long groupId,
				@Param("userId") Long userId,
				@Param("upToMessageId") Long upToMessageId,
				Pageable pageable
		);

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