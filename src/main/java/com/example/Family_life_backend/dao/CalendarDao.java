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
	@Transactional
	@Modifying
	@Query(value = """
			INSERT INTO calendar_events
			(group_id, created_by, title, description, event_time, end_time, notify_before, created_at)
			VALUES
			(?1, ?2, ?3, ?4, ?5, ?6, ?7, NOW())
			""", nativeQuery = true)
	int insertCalendarEvent(Long groupId, Long createdBy, String title, String description, LocalDateTime eventTime,
			LocalDateTime endTime, Integer notifyBefore);

	// 更新事件
	@Transactional
	@Modifying
	@Query(value = """
			UPDATE calendar_events
			SET title = ?2,
			    description = ?3,
			    event_time = ?4,
			    end_time = ?5,
			    notify_before = ?6
			WHERE id = ?1
			""", nativeQuery = true)
	int updateCalendarEvent(Long id, String title, String description, LocalDateTime eventTime, LocalDateTime endTime,
			Integer notifyBefore);

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
	@Query(value = "SELECT * FROM calendar_events WHERE event_time >= NOW() ORDER BY event_time ASC", nativeQuery = true)
	List<Calendar> findUpcomingEvents();

	// 2026-05- 24 by ZJ 查詢群組
	@Query(value = """
			    SELECT * FROM calendar_events
			    WHERE (:groupId IS NULL OR group_id = :groupId)
			    AND (:userId IS NULL OR created_by = :userId)
			""", nativeQuery = true)
	public List<Calendar> findExpenses(@Param("groupId") Long groupId, @Param("userId") Long userId);

// 查私人 2026-05-24 by ZJ
	@Query(value = "Select * from calendar_events where created_by = :userId and group_id is null", nativeQuery = true)
	public List<Calendar> findPersonalExpenses(@Param("userId") Long userId);
}
