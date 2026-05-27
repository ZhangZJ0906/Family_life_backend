package com.example.Family_life_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.CalendarDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Calendar;
import com.example.Family_life_backend.request.CalendarReq;
import com.example.Family_life_backend.response.CalendarRes;

@Service
public class CalendarService {

	@Autowired
	private CalendarDao calendarDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	// 新增事件
	public CalendarRes create(CalendarReq req) {

		if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
			return new CalendarRes(400, "開始時間不可大於結束時間");
		}

		int result = calendarDao.insertCalendarEvent(req.getGroupId(), req.getCreatedBy(), req.getTitle(),
				req.getDescription(), req.getEventTime(), req.getEndTime(), req.getNotifyBefore());

		// 建立行事曆通知給特定群組成員
		if (req.getGroupId() != 0) {
			List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
			String content = groupDao.getSelfName((long) req.getCreatedBy()) + "已新增" + req.getTitle();
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getCreatedBy()) {
					calendarDao.insertCalendarEventNotify(req.getGroupId(), member.getUser_id(), content, "calendar",
							false);

					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}
		}

		if (result > 0) {
			return new CalendarRes(200, "新增成功");
		}

		return new CalendarRes(500, "新增失敗");
	}

	// 修改事件
	public CalendarRes update(Long id, CalendarReq req) {

		if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
			return new CalendarRes(400, "開始時間不可大於結束時間");
		}

		int result = calendarDao.updateCalendarEvent(id, req.getTitle(), req.getDescription(), req.getEventTime(),
				req.getEndTime(), req.getNotifyBefore());

		if (result > 0) {
			// 發修改通知
			if (req.getGroupId() != 0) {
				String oldCalendarTitle = calendarDao.getCalendarNameById(id);
				List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
				String content = groupDao.getSelfName((long) req.getCreatedBy()) + "修改" + oldCalendarTitle + "行事曆";

				for (groupMembersDTO member : getGroupMembers) {
					if (member.getUser_id() != (long) req.getCreatedBy()) {
						calendarDao.insertCalendarEventNotify(req.getGroupId(), member.getUser_id(), content, "update",
								false);
						// 🔥 正確：要重新查 unread count
						int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

						notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
					}
				}
			}

			return new CalendarRes(200, "修改成功");
		}

		return new CalendarRes(404, "查無此事件");
	}

	// 刪除事件
	public CalendarRes delete(Long id, Long userId, Long groupId) {
		String oldCalendarTitle = calendarDao.getCalendarNameById(id);
		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);
		String content = groupDao.getSelfName(userId) + "刪除" + oldCalendarTitle;

		for (groupMembersDTO member : getGroupMembers) {
			if (member.getUser_id() != userId) {
				calendarDao.insertCalendarEventNotify(groupId, member.getUser_id(), content, "update", false);
				// 🔥 正確：要重新查 unread count
				int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

				notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
			}
		}

		int result = calendarDao.deleteCalendarEvent(id);

		if (result > 0) {
			return new CalendarRes(200, "刪除成功");
		}

		return new CalendarRes(404, "查無此事件");
	}

	// 查詢某一個家庭群組的所有行事曆事件
	public CalendarRes getByGroup(Long groupId) {
		List<Calendar> list = new ArrayList<Calendar>();
		if (groupId != 0) {
			list = calendarDao.findByGroupIdOrderByEventTimeAsc(groupId);
		}
		return new CalendarRes(200, "查詢成功", list);
	}

	// 2026-05- 24 by ZJ 新get 資訊
	public CalendarRes getCalendarEvents(Long groupId, Long userId) {

		// 私人活動
		if (groupId == 0) {
			if (userId == null || userId <= 0) {
				return new CalendarRes(400, "userId 錯誤");
			}
			List<Calendar> result = calendarDao.findPersonalExpenses(userId);
			return new CalendarRes(200, "查詢成功", result);
		}

		// 群組活動
		if (userId == null || userId <= 0) {
			return new CalendarRes(400, "userId 錯誤");
		}

		List<Calendar> result = calendarDao.findExpenses(groupId, null);
		return new CalendarRes(200, "查詢成功", result);
	}

	public CalendarRes getById(Long id) {
		Optional<Calendar> op = calendarDao.findById(id);

		if (op.isEmpty()) {
			return new CalendarRes(404, "查無此事件");
		}

		return new CalendarRes(200, "查詢成功", op.get());
	}
}
