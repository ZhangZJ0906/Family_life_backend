package com.example.Family_life_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.CalendarDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Calendar;
import com.example.Family_life_backend.request.CalendarReq;
import com.example.Family_life_backend.response.CalendarRes;

@Service
public class CalendarService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private CalendarDao calendarDao;

	@Autowired
	private UserInfoDao userInfoDao;

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

	    if (req.getCreatedBy() == null) {
	        return new CalendarRes(400, "createdBy 不可為空");
	    }

	    if (req.getTitle() == null || req.getTitle().isBlank()) {
	        return new CalendarRes(400, "活動名稱不可為空");
	    }

	    if (req.getEventTime() == null) {
	        return new CalendarRes(400, "活動時間不可為空");
	    }

	    if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
	        return new CalendarRes(400, "開始時間不可大於結束時間");
	    }

	    if (req.getEventTime().toLocalDate().isBefore(java.time.LocalDate.now())) {
	        return new CalendarRes(400, "開始日期不可早於今天");
	    }

	    Long groupId = req.getGroupId() == null ? 0L : req.getGroupId();
	    Long assignedUserId;

	    if (groupId == 0) {
	        // 私人活動：不用選成員，直接指派給建立者
	        assignedUserId = req.getCreatedBy();
	    } else {
	        // 群組活動：必須選擇指派成員
	        if (req.getAssignedUserId() == null) {
	            return new CalendarRes(400, "請選擇指派成員");
	        }

	        // Native Query 不要直接回傳 boolean，改用 count 判斷
	        int memberCount = groupMemberDao.countByGroupIdAndUserId(
	            groupId,
	            req.getAssignedUserId()
	        );

	        if (memberCount <= 0) {
	            return new CalendarRes(400, "此使用者不是該群組成員");
	        }

	        assignedUserId = req.getAssignedUserId();
	    }

	    int result = calendarDao.insertCalendarEvent(
	        groupId,
	        req.getCreatedBy(),
	        assignedUserId,
	        req.getTitle(),
	        req.getDescription(),
	        req.getEventTime(),
	        req.getEndTime(),
	        req.getNotifyBefore()
	    );

	    if (result <= 0) {
	        return new CalendarRes(500, "新增失敗");
	    }

	    // 建立行事曆通知給群組成員
	    if (groupId != 0) {
	        List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);
	        String content = groupDao.getSelfName(req.getCreatedBy()) + "已新增" + req.getTitle();

	        for (groupMembersDTO member : getGroupMembers) {
	            if (member.getUser_id() != req.getCreatedBy()) {
	                calendarDao.insertCalendarEventNotify(
	                    groupId,
	                    member.getUser_id(),
	                    content,
	                    "calendar",
	                    false
	                );

	                if (userInfoDao.getEmailNotifyById(member.getUser_id()) == true) {
	                    emailService.sendMail(
	                        userInfoDao.getEmailById(member.getUser_id()),
	                        "群組通知",
	                        content
	                    );
	                }

	                int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());
	                notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
	            }
	        }
	    }

	    return new CalendarRes(200, "新增成功");
	}

	// 修改事件
	public CalendarRes update(Long id, CalendarReq req) {

	    if (req.getCreatedBy() == null) {
	        return new CalendarRes(400, "createdBy 不可為空");
	    }

	    if (req.getTitle() == null || req.getTitle().isBlank()) {
	        return new CalendarRes(400, "活動名稱不可為空");
	    }

	    if (req.getEventTime() == null) {
	        return new CalendarRes(400, "活動時間不可為空");
	    }

	    if (req.getEventTime().toLocalDate().isBefore(java.time.LocalDate.now())) {
	        return new CalendarRes(400, "開始日期不可早於今天");
	    }

	    if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
	        return new CalendarRes(400, "開始時間不可大於結束時間");
	    }

	    Long groupId = req.getGroupId() == null ? 0L : req.getGroupId();
	    Long assignedUserId;

	    if (groupId == 0) {
	        assignedUserId = req.getCreatedBy();
	    } else {
	        if (req.getAssignedUserId() == null) {
	            return new CalendarRes(400, "請選擇指派成員");
	        }

	        int memberCount = groupMemberDao.countByGroupIdAndUserId(
	            groupId,
	            req.getAssignedUserId()
	        );

	        if (memberCount <= 0) {
	            return new CalendarRes(400, "此使用者不是該群組成員");
	        }

	        assignedUserId = req.getAssignedUserId();
	    }

	    String oldCalendarTitle = calendarDao.getCalendarNameById(id);

	    int result = calendarDao.updateCalendarEvent(
	        id,
	        req.getTitle(),
	        req.getDescription(),
	        req.getEventTime(),
	        req.getEndTime(),
	        req.getNotifyBefore(),
	        assignedUserId
	    );

	    if (result <= 0) {
	        return new CalendarRes(404, "查無此事件");
	    }

	    if (groupId != 0) {
	        List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);
	        String content = groupDao.getSelfName(req.getCreatedBy()) + "修改" + oldCalendarTitle + "行事曆";

	        for (groupMembersDTO member : getGroupMembers) {
	            if (member.getUser_id() != req.getCreatedBy()) {
	                calendarDao.insertCalendarEventNotify(
	                    groupId,
	                    member.getUser_id(),
	                    content,
	                    "update",
	                    false
	                );

	                if (userInfoDao.getEmailNotifyById(member.getUser_id()) == true) {
	                    emailService.sendMail(
	                        userInfoDao.getEmailById(member.getUser_id()),
	                        "群組通知",
	                        content
	                    );
	                }

	                int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());
	                notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
	            }
	        }
	    }

	    return new CalendarRes(200, "修改成功");
	}
	// 刪除事件
	public CalendarRes delete(Long id, Long userId, Long groupId) {
		String oldCalendarTitle = calendarDao.getCalendarNameById(id);
		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);
		String content = groupDao.getSelfName(userId) + "刪除" + oldCalendarTitle;

		for (groupMembersDTO member : getGroupMembers) {
			if (member.getUser_id() != userId) {
				calendarDao.insertCalendarEventNotify(groupId, member.getUser_id(), content, "update", false);

				if (userInfoDao.getEmailNotifyById(member.getUser_id()) == true) {
					emailService.sendMail(userInfoDao.getEmailById(member.getUser_id()), "群組通知", content);
				}
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

	public CalendarRes getByGroup(Long groupId, Long userId) {

	    if (userId == null) {
	        return new CalendarRes(400, "userId 不可為空");
	    }

	    Long realGroupId = groupId == null ? 0L : groupId;

	    List<Calendar> list;

	    if (realGroupId == 0) {
	        // 私人行事曆：只查自己的
	        list = calendarDao.findPrivateCalendarByUserId(userId);
	    } else {
	        // 群組行事曆：只查指派給自己的
	        list = calendarDao.findByGroupIdAndAssignedUserIdOrderByEventTimeAsc(
	            realGroupId,
	            userId
	        );
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
	
	// 查詢群組中，指派給目前登入者的活動
	public CalendarRes getGroupCalendarByAssignedUser(Long groupId, Long userId) {

	    if (groupId == null || groupId <= 0) {
	        return new CalendarRes(400, "groupId 不可為空");
	    }

	    if (userId == null) {
	        return new CalendarRes(400, "userId 不可為空");
	    }

	    List<Calendar> list =
	            calendarDao.findByGroupIdAndAssignedUserIdOrderByEventTimeAsc(groupId, userId);

	    return new CalendarRes(200, "查詢成功", list);
	}
}
