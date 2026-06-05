package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
	
	 // 台灣時區
    private static final ZoneId TAIWAN_ZONE = ZoneId.of("Asia/Taipei");

 // 每 60 秒檢查一次行事曆通知
	@Scheduled(fixedRate = 60000)
	public void checkEventTime() {

		// 原本是 LocalDateTime.now()
        // 雲端主機如果是美國時區，會拿到美國時間
        // 改成固定使用台灣時間
        LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

		sendBeforeNotify(now);

		sendStartNotify(now);
	}

	// 活動開始前提醒
	private void sendBeforeNotify(LocalDateTime now) {

		List<Calendar> events = calendarDao.findEventsBeforeToNotify(now);

		for (Calendar event : events) {

			String content = "提醒：距離活動" + event.getTitle() + "還有" + event.getNotifyBefore() + "分鐘";

			insertNotify(event, content);

			pushUnread(event.getAssignedUserId());

			calendarDao.markBeforeAsNotified(event.getId());
		}
	}

	// 活動開始時提醒
	private void sendStartNotify(LocalDateTime now) {

		List<Calendar> events = calendarDao.findEventsStartToNotify(now);

		for (Calendar event : events) {

			String content = "提醒：" + event.getTitle() + "已經開始";

			insertNotify(event, content);

			pushUnread(event.getAssignedUserId());

			calendarDao.markStartAsNotified(event.getId());
		}
	}

	 // 寫入通知資料表
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
