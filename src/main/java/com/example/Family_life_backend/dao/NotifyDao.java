package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.DTO.UserNotifyDTO;
import com.example.Family_life_backend.entity.notify;
import com.example.Family_life_backend.response.BasicResponse;

@Repository
public interface NotifyDao extends JpaRepository<notify, Long>{
	
	@Modifying
	@Transactional
	@Query(value = "delete from notify n where n.notify_id = :notifyId", nativeQuery = true)
	public void deleteNotify(@Param("notifyId") Long notifyId);
	
	@Modifying
	@Transactional
	@Query(value = """
	UPDATE notify n
	SET is_read = 1
	WHERE n.notify_id = :notifyId
	""", nativeQuery = true)
	public void isReadNotify(@Param("notifyId") Long notifyId);
		
	@Modifying
	@Transactional
	@Query(value = "update notify n set status = :status where n.get_user_id = :userId and n.notify_id = :notifyId", nativeQuery = true)
	public int updateInviteNotify(@Param("status") String status, @Param("userId") Long userId, @Param("notifyId") Long notifyId);
	
	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (send_id, get_user_id, content, type, is_read, target_group_id)
			    values (:sendUserId, :getUserId, :content, :type, :isRead, :targetGroupId)
			""", nativeQuery = true)
	public void sendNewMemberNotify(@Param("sendUserId") Long sendUserId, @Param("getUserId") Long getUserId, @Param("content") String content
			, @Param("type") String type, @Param("isRead") boolean isRead, @Param("targetGroupId") Long targretGroupId);
	
	@Modifying
	@Transactional
	@Query(value = """
		    insert into notify (send_id, get_user_id, content, type, is_read)
		    values (:sendId, :getUserId, :content, :type, :isRead)
		""", nativeQuery = true)
	public void sendGroupNameUpdateNotify(@Param("sendId") Long sendId, @Param("getUserId") Long getUserId, @Param("content") String content
			, @Param("type") String type, @Param("isRead") boolean isRead);
	
	@Query(value = """
		    select count(*) from notify where get_user_id = :getUserId
		""", nativeQuery = true)
	public int getNotifyCount(@Param("getUserId") Long getUserId);
}
