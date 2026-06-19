package com.example.Family_life_backend.repositary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Family_life_backend.DTO.GroupChatReadCountDTO;
import com.example.Family_life_backend.entity.GroupChatRead;

public interface GroupChatReadRepository extends JpaRepository<GroupChatRead, Long> {

	long countByMessageId(Long messageId);

	boolean existsByMessageIdAndUserId(Long messageId, Long userId);

	List<GroupChatRead> findByMessageIdInAndUserId(List<Long> messageIds, Long userId);

	@Query("""
			SELECT new com.example.Family_life_backend.DTO.GroupChatReadCountDTO(
			    r.messageId,
			    COUNT(r)
			)
			FROM GroupChatRead r
			WHERE r.messageId IN :messageIds
			GROUP BY r.messageId
			""")
	List<GroupChatReadCountDTO> countByMessageIds(@Param("messageIds") List<Long> messageIds);
}