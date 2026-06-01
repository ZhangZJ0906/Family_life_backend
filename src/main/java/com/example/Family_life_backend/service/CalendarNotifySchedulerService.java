package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.CalendarDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.entity.Calendar;

@Service
public class CalendarNotifySchedulerService {
	@Autowired
	private CalendarDao calendarDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	@Scheduled(fixedRate = 60000) // 每 60 秒檢查一次
	public void checkEventTime() {

		LocalDateTime now = LocalDateTime.now();

		List<Calendar> list = calendarDao.findEventsToNotify(now);

		for (Calendar event : list) {

			String content = "提醒：即將開始活動" + event.getTitle();

			// 1. 寫通知
			if (event.getGroupId() == 0) { //私人活動
				calendarDao.insertCalendarEventNotify(event.getAssignedUserId(), event.getAssignedUserId(), content,
						"calendar_self", false);
			} else {
				calendarDao.insertCalendarEventNotify(event.getGroupId(), event.getAssignedUserId(), content,
						"calendar", false);
			}

			// 2. websocket 推送
			int unread = notifyDao.countUnreadByUserId(event.getAssignedUserId());
			notifySocketService.pushUnreadCount(event.getAssignedUserId(), unread);

			// 3. 標記已通知
			calendarDao.markAsNotified(event.getId());
		}
	}
}
