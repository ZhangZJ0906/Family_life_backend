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

	@Scheduled(fixedRate = 60000)
	public void checkEventTime() {

		LocalDateTime now = LocalDateTime.now();

		sendBeforeNotify(now);

		sendStartNotify(now);
	}

	private void sendBeforeNotify(LocalDateTime now) {

		List<Calendar> events = calendarDao.findEventsBeforeToNotify(now);

		for (Calendar event : events) {

			String content = "提醒：距離活動" + event.getTitle() + "還有" + event.getNotifyBefore() + "分鐘";

			insertNotify(event, content);

			pushUnread(event.getAssignedUserId());

			calendarDao.markBeforeAsNotified(event.getId());
		}
	}

	private void sendStartNotify(LocalDateTime now) {

		List<Calendar> events = calendarDao.findEventsStartToNotify(now);

		for (Calendar event : events) {

			String content = "提醒：" + event.getTitle() + "已經開始";

			insertNotify(event, content);

			pushUnread(event.getAssignedUserId());

			calendarDao.markStartAsNotified(event.getId());
		}
	}

	private void insertNotify(Calendar event, String content) {

		if (event.getGroupId() == 0) {

			calendarDao.insertCalendarEventNotify(event.getAssignedUserId(), event.getAssignedUserId(), content,
					"calendar_self", false);

		} else {

			calendarDao.insertCalendarEventNotify(event.getGroupId(), event.getAssignedUserId(), content, "calendar",
					false);
		}
	}

	private void pushUnread(Long userId) {

		int unread = notifyDao.countUnreadByUserId(userId);

		notifySocketService.pushUnreadCount(userId, unread);
	}
}
