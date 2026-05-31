package com.example.Family_life_backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

	    // =========================
	    // 1. 基本欄位驗證
	    // =========================
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

	    // groupId = 0 代表私人活動
	    Long groupId = req.getGroupId() == null ? 0L : req.getGroupId();

	    // =========================
	    // 2. 決定要指派給哪些成員
	    // =========================
	    List<Long> assignedUserIds = new ArrayList<>();

	    if (groupId == 0) {
	        // 私人活動：不用選成員，直接指派給建立者自己
	        assignedUserIds.add(req.getCreatedBy());
	    } else {
	        // 群組活動：優先使用前端複選傳來的 assignedUserIds
	        if (req.getAssignedUserIds() != null && !req.getAssignedUserIds().isEmpty()) {
	            assignedUserIds.addAll(req.getAssignedUserIds());
	        }

	        // 保險：如果前端舊版只傳 assignedUserId，也可以接住
	        if (assignedUserIds.isEmpty() && req.getAssignedUserId() != null) {
	            assignedUserIds.add(req.getAssignedUserId());
	        }

	        // 如果還是空，代表前端沒有成功送出指派成員
	        if (assignedUserIds.isEmpty()) {
	            return new CalendarRes(400, "請選擇指派成員");
	        }
	    }

	    // =========================
	    // 3. 去除重複成員
	    // 例如前端不小心送 [1, 1, 2]
	    // =========================
	    assignedUserIds = assignedUserIds
	            .stream()
	            .distinct()
	            .toList();

	    // =========================
	    // 4. 檢查群組活動的每位指派成員是否真的在群組內
	    // =========================
	    if (groupId != 0) {
	        for (Long assignedUserId : assignedUserIds) {

	            int memberCount = groupMemberDao.countByGroupIdAndUserId(
	                    groupId,
	                    assignedUserId
	            );

	            if (memberCount <= 0) {
	                return new CalendarRes(400, "有指派成員不屬於該群組");
	            }
	        }
	    }

	 // 同一次新增的多位成員活動，共用同一個批次 ID
	    String eventBatchId = UUID.randomUUID().toString();
	    // =========================
	    // 5. 勾選幾位成員，就新增幾筆 calendar_events
	    // =========================
	    int successCount = 0;

	    for (Long assignedUserId : assignedUserIds) {

	    	int result = calendarDao.insertCalendarEvent(
	    	        eventBatchId,
	    	        groupId,
	    	        req.getCreatedBy(),
	    	        assignedUserId,
	    	        req.getTitle(),
	    	        req.getDescription(),
	    	        req.getEventTime(),
	    	        req.getEndTime(),
	    	        req.getNotifyBefore()
	    	);

	        successCount += result;
	    }

	    if (successCount <= 0) {
	        return new CalendarRes(500, "新增失敗");
	    }

	    // =========================
	    // 6. 發送通知給「被指派的成員」
	    // =========================
	    if (groupId != 0) {

	        String content = groupDao.getSelfName(req.getCreatedBy())
	                + "已新增行事曆活動："
	                + req.getTitle();

	        for (Long assignedUserId : assignedUserIds) {

	            // 寫入通知資料表
	            calendarDao.insertCalendarEventNotify(
	                    groupId,
	                    assignedUserId,
	                    content,
	                    "calendar",
	                    false
	            );

	            // 如果該使用者有開 Email 通知，就寄信
	            if (userInfoDao.getEmailNotifyById(assignedUserId) == true) {
	                emailService.sendMail(
	                        userInfoDao.getEmailById(assignedUserId),
	                        "群組通知",
	                        content
	                );
	            }

	            // WebSocket 推送未讀通知數
	            int unreadCount = notifyDao.countUnreadByUserId(assignedUserId);
	            notifySocketService.pushUnreadCount(assignedUserId, unreadCount);
	        }
	    }

	    return new CalendarRes(200, "新增成功");
	}
	// 修改事件：同步修改同一批活動
	public CalendarRes update(Long id, CalendarReq req) {

	    // =========================
	    // 1. 基本驗證
	    // =========================
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

	    // =========================
	    // 2. 找出目前這筆活動
	    // =========================
	    Optional<Calendar> oldOp = calendarDao.findById(id);

	    if (oldOp.isEmpty()) {
	        return new CalendarRes(404, "查無此事件");
	    }

	    Calendar oldEvent = oldOp.get();

	    // 如果舊資料沒有 eventBatchId，就用自己的 id 當作一批
	    String eventBatchId = oldEvent.getEventBatchId();

	    if (eventBatchId == null || eventBatchId.isBlank()) {
	        eventBatchId = java.util.UUID.randomUUID().toString();
	    }

	    String oldCalendarTitle = oldEvent.getTitle();

	    // =========================
	    // 3. 決定修改後要指派給哪些成員
	    // =========================
	    List<Long> assignedUserIds = new ArrayList<>();

	    if (groupId == 0) {
	        // 私人活動：只能指派給自己
	        assignedUserIds.add(req.getCreatedBy());
	    } else {
	        // 群組活動：優先使用複選成員清單
	        if (req.getAssignedUserIds() != null && !req.getAssignedUserIds().isEmpty()) {
	            assignedUserIds.addAll(req.getAssignedUserIds());
	        }

	        // 保險：如果前端只送單一 assignedUserId，也能接
	        if (assignedUserIds.isEmpty() && req.getAssignedUserId() != null) {
	            assignedUserIds.add(req.getAssignedUserId());
	        }

	        if (assignedUserIds.isEmpty()) {
	            return new CalendarRes(400, "請選擇指派成員");
	        }
	    }

	    // 去除重複成員
	    assignedUserIds = assignedUserIds
	            .stream()
	            .distinct()
	            .toList();

	    // =========================
	    // 4. 檢查每位指派成員是否屬於該群組
	    // =========================
	    if (groupId != 0) {
	        for (Long assignedUserId : assignedUserIds) {

	            int memberCount = groupMemberDao.countByGroupIdAndUserId(
	                    groupId,
	                    assignedUserId
	            );

	            if (memberCount <= 0) {
	                return new CalendarRes(400, "有指派成員不屬於該群組");
	            }
	        }
	    }

	    // =========================
	    // 5. 刪除同一批舊活動
	    // =========================
	    calendarDao.deleteByEventBatchId(eventBatchId);

	    // =========================
	    // 6. 依照修改後勾選成員，重新建立同一批活動
	    // =========================
	    int successCount = 0;

	    for (Long assignedUserId : assignedUserIds) {

	        int result = calendarDao.insertCalendarEvent(
	                eventBatchId,
	                groupId,
	                req.getCreatedBy(),
	                assignedUserId,
	                req.getTitle(),
	                req.getDescription(),
	                req.getEventTime(),
	                req.getEndTime(),
	                req.getNotifyBefore()
	        );

	        successCount += result;
	    }

	    if (successCount <= 0) {
	        return new CalendarRes(500, "修改失敗");
	    }

	    // =========================
	    // 7. 發送通知給被指派成員
	    // =========================
	    if (groupId != 0) {

	        String content = groupDao.getSelfName(req.getCreatedBy())
	                + "修改行事曆活動："
	                + oldCalendarTitle
	                + " → "
	                + req.getTitle();

	        for (Long assignedUserId : assignedUserIds) {

	            calendarDao.insertCalendarEventNotify(
	                    groupId,
	                    assignedUserId,
	                    content,
	                    "update",
	                    false
	            );

	            if (userInfoDao.getEmailNotifyById(assignedUserId) == true) {
	                emailService.sendMail(
	                        userInfoDao.getEmailById(assignedUserId),
	                        "群組通知",
	                        content
	                );
	            }

	            int unreadCount = notifyDao.countUnreadByUserId(assignedUserId);
	            notifySocketService.pushUnreadCount(assignedUserId, unreadCount);
	        }
	    }

	    return new CalendarRes(200, "修改成功");
	}
	// 刪除事件：如果有 eventBatchId，就刪除同一批活動
	public CalendarRes delete(Long id, Long userId, Long groupId) {

	    Optional<Calendar> oldOp = calendarDao.findById(id);

	    if (oldOp.isEmpty()) {
	        return new CalendarRes(404, "查無此事件");
	    }

	    Calendar oldEvent = oldOp.get();

	    String oldCalendarTitle = oldEvent.getTitle();
	    String eventBatchId = oldEvent.getEventBatchId();

	    String content = groupDao.getSelfName(userId) + "刪除" + oldCalendarTitle;

	    // 群組活動才發通知
	    if (groupId != null && groupId != 0) {
	        List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);

	        for (groupMembersDTO member : getGroupMembers) {
	            if (!java.util.Objects.equals(member.getUser_id(), userId)) {
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

	    int result;

	    if (eventBatchId != null && !eventBatchId.isBlank()) {
	        // 刪除同一批活動
	        result = calendarDao.deleteByEventBatchId(eventBatchId);
	    } else {
	        // 舊資料沒有 eventBatchId，就只刪單筆
	        result = calendarDao.deleteCalendarEvent(id);
	    }

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
	
	// 查詢同一批活動目前指派的成員 ID
	public CalendarRes getBatchAssignedUsers(String eventBatchId) {

	    if (eventBatchId == null || eventBatchId.isBlank()) {
	        return new CalendarRes(400, "eventBatchId 不可為空");
	    }

	    List<Long> assignedUserIds =
	            calendarDao.findAssignedUserIdsByEventBatchId(eventBatchId);

	    return new CalendarRes(200, "查詢成功", assignedUserIds);
	}
	
	
	
}
