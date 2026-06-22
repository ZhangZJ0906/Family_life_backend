package com.example.Family_life_backend.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.entity.notify;

@Repository
public interface NotifyDao extends JpaRepository<notify, Long> {

	@Modifying
	@Transactional
	@Query(value = "delete from notify n where n.notify_id = :notifyId", nativeQuery = true)
	public void deleteOneNotify(@Param("notifyId") Long notifyId);

	@Modifying
	@Transactional
	@Query(value = """
			    DELETE FROM notify n
			    WHERE n.notify_id IN :ids
			""", nativeQuery = true)
	int batchDeleteNotify(@Param("ids") List<Long> ids);

	@Modifying
	@Transactional
	@Query(value = """
			UPDATE notify n
			SET is_read = 1
			WHERE n.notify_id = :notifyId
			""", nativeQuery = true)
	public void isReadOneNotify(@Param("notifyId") Long notifyId);

	@Modifying
	@Query(value = """
			    UPDATE notify n
			    SET n.is_read = 1
			    WHERE n.notify_id IN :ids and n.type != 'invite'
			""", nativeQuery = true)
	int batchReadNotify(@Param("ids") List<Long> ids);

	@Modifying
	@Transactional
	@Query(value = "update notify n set status = :status where n.get_user_id = :userId and n.notify_id = :notifyId", nativeQuery = true)
	public int updateInviteNotify(@Param("status") String status, @Param("userId") Long userId,
			@Param("notifyId") Long notifyId);

	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (
			        send_id,
			        get_user_id,
			        content,
			        type,
			        is_read,
			        target_group_id,
			        send_date
			    )
			    values (
			        :sendUserId,
			        :getUserId,
			        :content,
			        :type,
			        :isRead,
			        :targetGroupId,
			        :sendDate
			    )
			""", nativeQuery = true)
	public void sendNewMemberNotify(@Param("sendUserId") Long sendUserId, @Param("getUserId") Long getUserId,
			@Param("content") String content, @Param("type") String type, @Param("isRead") boolean isRead,
			@Param("targetGroupId") Long targetGroupId, @Param("sendDate") LocalDateTime sendDate);

	@Modifying
	@Transactional
	@Query(value = """

			    insert into notify (send_id, get_user_id, content, type, is_read,send_date)
			    values (:sendId, :getUserId, :content, :type, :isRead, :send_date)
			""", nativeQuery = true)
	public void sendGroupNameUpdateNotify(@Param("sendId") Long sendId, @Param("getUserId") Long getUserId,
			@Param("content") String content, @Param("type") String type, @Param("isRead") boolean isRead,
			@Param("send_date") LocalDateTime send_date);

	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (send_id, get_user_id, content, type, is_read)
			    values (:sendId, :getUserId, :content, :type, :isRead)
			""", nativeQuery = true)
	public void sendChatNotify(@Param("sendId") Long sendId, @Param("getUserId") Long getUserId,
			@Param("content") String content, @Param("type") String type, @Param("isRead") boolean isRead);

	@Query(value = """
			    select count(*) from notify where get_user_id = :getUserId
			""", nativeQuery = true)
	public int getNotifyCount(@Param("getUserId") Long getUserId);

	@Query(value = """
			    select count(*) from notify where get_user_id = :getUserId and is_read = 0
			""", nativeQuery = true)
	public int countUnreadByUserId(@Param("getUserId") Long getUserId);

	// 批次
	@Query(value = """
						SELECT get_user_id, COUNT(*)
						FROM notify
						WHERE get_user_id IN :userIds AND is_read = 0
			GROUP BY get_user_id
						""", nativeQuery = true)
	List<Object[]> countUnreadByUserIds(@Param("userIds") List<Long> userIds);
}
