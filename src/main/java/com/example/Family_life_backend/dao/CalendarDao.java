package com.example.Family_life_backend.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.entity.Calendar;

@Repository
public interface CalendarDao extends JpaRepository<Calendar, Long> {

	// 新增事件
	@Modifying
	@Transactional
	@Query(value = """
			INSERT INTO calendar_events
			(
			    event_batch_id,
			    group_id,
			    created_by,
			    assigned_user_id,
			    title,
			    description,
			    event_time,
			    end_time,
			    notify_before,
			    created_at
			)
			VALUES
			(
			    :eventBatchId,
			    :groupId,
			    :createdBy,
			    :assignedUserId,
			    :title,
			    :description,
			    :eventTime,
			    :endTime,
			    :notifyBefore,
			    :createdAt
			)
			""", nativeQuery = true)
	int insertCalendarEvent(@Param("eventBatchId") String eventBatchId, @Param("groupId") Long groupId,
			@Param("createdBy") Long createdBy, @Param("assignedUserId") Long assignedUserId,
			@Param("title") String title, @Param("description") String description,
			@Param("eventTime") LocalDateTime eventTime, @Param("endTime") LocalDateTime endTime,
			@Param("notifyBefore") Integer notifyBefore, @Param("createdAt") LocalDateTime createdAt);

	// 更新事件
	@Modifying
	@Transactional
	@Query(value = """
			UPDATE calendar_events
			SET
			    title = :title,
			    description = :description,
			    event_time = :eventTime,
			    end_time = :endTime,
			    notify_before = :notifyBefore,
			    assigned_user_id = :assignedUserId
			WHERE id = :id
			""", nativeQuery = true)
	int updateCalendarEvent(@Param("id") Long id, @Param("title") String title,
			@Param("description") String description, @Param("eventTime") LocalDateTime eventTime,
			@Param("endTime") LocalDateTime endTime, @Param("notifyBefore") Integer notifyBefore,
			@Param("assignedUserId") Long assignedUserId);

	// 找行事曆名字
	@Query(value = "select title from calendar_events where id = :id", nativeQuery = true)
	public String getCalendarNameById(@Param("id") Long id);

	// 通知
	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (send_id, get_user_id, content, type, is_read)
			    values (:sendId, :getUserId, :content, :type, :isRead)
			""", nativeQuery = true)
	public void insertCalendarEventNotify(@Param("sendId") Long sendId, @Param("getUserId") Long getUserId,
			@Param("content") String content, @Param("type") String type, @Param("isRead") boolean isRead);

	// 刪除事件
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM calendar_events WHERE id = ?1", nativeQuery = true)
	int deleteCalendarEvent(Long id);

	// 查詢某個家庭群組的所有行事曆事件，依照活動時間排序
	@Query(value = "SELECT * FROM calendar_events WHERE group_id = ?1 ORDER BY event_time ASC", nativeQuery = true)
	List<Calendar> findByGroupIdOrderByEventTimeAsc(Long groupId);

	// 查詢某個使用者建立的事件
	@Query(value = "SELECT * FROM calendar_events WHERE created_by = ?1 ORDER BY event_time ASC", nativeQuery = true)
	List<Calendar> findByCreatedBy(Long createdBy);

	// 查詢某個家庭群組中，某個使用者建立的事件
	@Query(value = "SELECT * FROM calendar_events WHERE group_id = ?1 AND created_by = ?2 ORDER BY event_time ASC", nativeQuery = true)
	List<Calendar> findByGroupIdAndCreatedBy(Long groupId, Long createdBy);

	// 查詢即將提醒的事件
	@Query(value = "SELECT * FROM calendar_events WHERE event_time >= :now ORDER BY event_time ASC", nativeQuery = true)
	List<Calendar> findUpcomingEvents(@Param("now") LocalDateTime now);

	// 2026-05- 24 by ZJ 查詢群組
	@Query(value = """
			    SELECT * FROM calendar_events
			    WHERE (:groupId IS NULL OR group_id = :groupId)
			    AND (:userId IS NULL OR created_by = :userId)
			""", nativeQuery = true)
	public List<Calendar> findExpenses(@Param("groupId") Long groupId, @Param("userId") Long userId);

	// 查私人 2026-05-24 by ZJ
	@Query(value = """
			SELECT *
			FROM calendar_events
			WHERE group_id = 0
			  AND assigned_user_id = :userId
			ORDER BY event_time ASC
			""", nativeQuery = true)
	List<Calendar> findPersonalExpenses(@Param("userId") Long userId);

	// 查詢某群組中，指派給指定使用者的活動
	@Query(value = """
			SELECT *
			FROM calendar_events
			WHERE group_id = :groupId
			  AND assigned_user_id = :userId
			ORDER BY event_time ASC
			""", nativeQuery = true)
	List<Calendar> findByGroupIdAndAssignedUserIdOrderByEventTimeAsc(@Param("groupId") Long groupId,
			@Param("userId") Long userId);

	// 查詢私人行事曆
	@Query(value = """
			SELECT *
			FROM calendar_events
			WHERE group_id = 0
			  AND assigned_user_id = :userId
			ORDER BY event_time ASC
			""", nativeQuery = true)
	List<Calendar> findPrivateCalendarByUserId(@Param("userId") Long userId);

	// 依照單筆活動 id 找出同批活動識別碼
	@Query(value = """
			SELECT event_batch_id
			FROM calendar_events
			WHERE id = :id
			""", nativeQuery = true)
	String findEventBatchIdById(@Param("id") Long id);

	// 查詢同一批活動目前指派了哪些成員
	@Query(value = """
			SELECT assigned_user_id
			FROM calendar_events
			WHERE event_batch_id = :eventBatchId
			ORDER BY assigned_user_id ASC
			""", nativeQuery = true)
	List<Long> findAssignedUserIdsByEventBatchId(@Param("eventBatchId") String eventBatchId);

	// 刪除同一批活動
	@Modifying
	@Transactional
	@Query(value = """
			DELETE FROM calendar_events
			WHERE event_batch_id = :eventBatchId
			""", nativeQuery = true)
	int deleteByEventBatchId(@Param("eventBatchId") String eventBatchId);

	// 依照批次 ID 查詢同一批活動
	@Query(value = """
			SELECT *
			FROM calendar_events
			WHERE event_batch_id = :eventBatchId
			ORDER BY assigned_user_id ASC
			""", nativeQuery = true)
	List<Calendar> findByEventBatchId(@Param("eventBatchId") String eventBatchId);

	// 提前提醒
	@Query(value = """
					SELECT *
			FROM calendar_events
			WHERE DATE_SUB(event_time, INTERVAL notify_before MINUTE) <= :now
			AND is_send_before_notify = 0 and notify_before != 0
					""", nativeQuery = true)
	List<Calendar> findEventsBeforeToNotify(@Param("now") LocalDateTime now);

	// 開始提醒
	@Query(value = """
					SELECT *
			FROM calendar_events
			WHERE event_time <= :now
			AND is_send_start_notify = 0
					""", nativeQuery = true)
	List<Calendar> findEventsStartToNotify(@Param("now") LocalDateTime now);

	// 已發送通知不再重複送
	@Modifying
	@Transactional
	@Query(value = """
			UPDATE calendar_events SET is_send_before_notify = 1 WHERE id = :eventId
			""", nativeQuery = true)
	public void markBeforeAsNotified(@Param("eventId") Long eventId);
	
	@Modifying
	@Transactional
	@Query(value = """
			UPDATE calendar_events SET is_send_start_notify = 1 WHERE id = :eventId
			""", nativeQuery = true)
	public void markStartAsNotified(@Param("eventId") Long eventId);

	// 登入該page時間
	@Modifying
	@Transactional
	@Query(value = """
			update users set login_calendar_page_time = :now where user_id = :userId
			""", nativeQuery = true)
	public void recordLoginCalendarPageTime(@Param("userId") Long userId, @Param("now") LocalDateTime now);

	// 抓取上次登入page時間
	@Query(value = """
			select login_calendar_page_time from users where user_id = :userId
			""", nativeQuery = true)
	public LocalDateTime getLoginCalendarPageTime(@Param("userId") Long userId);
}
