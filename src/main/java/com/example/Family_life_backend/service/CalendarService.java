package com.example.Family_life_backend.service;

import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.EmailNotifyUserDTO;
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
	
	private static final ZoneId TAIWAN_ZONE = ZoneId.of("Asia/Taipei");
	
	private CalendarRes validateCalendarText(CalendarReq req) {

	    if (req.getTitle() == null || req.getTitle().isBlank()) {
	        return new CalendarRes(400, "活動名稱不可為空");
	    }

	    String title = req.getTitle().trim();

	    if (title.length() > 100) {
	        return new CalendarRes(400, "活動名稱不可超過 100 個字");
	    }

	    String description =
	            req.getDescription() == null
	                    ? ""
	                    : req.getDescription().trim();

	    if (description.length() > 2000) {
	        return new CalendarRes(400, "活動描述不可超過 2000 個字");
	    }

	    req.setTitle(title);
	    req.setDescription(description);

	    return null;
	}

	// 新增事件
	public CalendarRes create(CalendarReq req) {

		 CalendarRes validationError = validateCalendarText(req);

		    if (validationError != null) {
		        return validationError;
		    }
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

		if (req.getEventTime().toLocalDate().isBefore(LocalDate.now(TAIWAN_ZONE))) {
		    return new CalendarRes(400, "開始日期不可早於今天");
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
		assignedUserIds = assignedUserIds.stream().distinct().toList();

		// =========================
		// 4. 檢查群組活動的每位指派成員是否真的在群組內
		// =========================
		if (groupId != 0) {
			for (Long assignedUserId : assignedUserIds) {

				int memberCount = groupMemberDao.countByGroupIdAndUserId(groupId, assignedUserId);

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

			LocalDateTime taiwanNow = LocalDateTime.now(TAIWAN_ZONE);

			int result = calendarDao.insertCalendarEvent(
			    eventBatchId,
			    groupId,
			    req.getCreatedBy(),
			    assignedUserId,
			    req.getTitle(),
			    req.getDescription(),
			    req.getEventTime(),
			    req.getEndTime(),
			    req.getNotifyBefore(),
			    taiwanNow
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

			String content = groupDao.getSelfName(req.getCreatedBy()) + "已新增行事曆活動：" + req.getTitle();

			LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

			List<EmailNotifyUserDTO> notifyUsers =
			        userInfoDao.findEmailNotifyUsers(assignedUserIds);

			Set<Long> pushUserIds = new HashSet<>();

			for (Long assignedUserId : assignedUserIds) {

			    calendarDao.insertCalendarEventNotify(
			        groupId,
			        assignedUserId,
			        content,
			        "calendar",
			        false,
			        now
			    );

			    pushUserIds.add(assignedUserId);
			}

			for (EmailNotifyUserDTO user : notifyUsers) {
			    if (Boolean.TRUE.equals(user.getNotifyByEmail())) {
			        emailService.sendMailAsync(
			            user.getEmail(),
			            "群組通知",
			            content
			        );
			    }
			}

			for (Long pushUserId : pushUserIds) {
			    int unreadCount = notifyDao.countUnreadByUserId(pushUserId);
			    notifySocketService.pushUnreadCount(pushUserId, unreadCount);
			}
		}

		return new CalendarRes(200, "新增成功");
	}

	// 修改事件：同步修改同一批活動
	public CalendarRes update(Long id, CalendarReq req) {
		
		CalendarRes validationError = validateCalendarText(req);

	    if (validationError != null) {
	        return validationError;
	    }

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

	    if (req.getEventTime().toLocalDate().isBefore(LocalDate.now(TAIWAN_ZONE))) {
	        return new CalendarRes(400, "開始日期不可早於今天");
	    }

	    if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
	        return new CalendarRes(400, "開始時間不可大於結束時間");
	    }

	    Long groupId = req.getGroupId() == null ? 0L : req.getGroupId();

	    // =========================
	    // 2. 找出目前這筆活動
	    // id 找不到時，改用 eventBatchId 找
	    // =========================
	    Optional<Calendar> oldOp = calendarDao.findById(id);

	    Calendar oldEvent = null;

	    if (oldOp.isPresent()) {
	        oldEvent = oldOp.get();
	    } else if (req.getEventBatchId() != null && !req.getEventBatchId().isBlank()) {
	        List<Calendar> batchEvents = calendarDao.findByEventBatchId(req.getEventBatchId());

	        if (!batchEvents.isEmpty()) {
	            oldEvent = batchEvents.get(0);
	        }
	    }

	    if (oldEvent == null) {
	        return new CalendarRes(404, "查無此事件");
	    }

	    String eventBatchId = oldEvent.getEventBatchId();

	    if (eventBatchId == null || eventBatchId.isBlank()) {
	        eventBatchId = req.getEventBatchId();
	    }

	    if (eventBatchId == null || eventBatchId.isBlank()) {
	        eventBatchId = UUID.randomUUID().toString();
	    }

	    String oldCalendarTitle = oldEvent.getTitle();

	    // =========================
	    // 3. 決定修改後要指派給哪些成員
	    // =========================
	    List<Long> assignedUserIds = new ArrayList<>();

	    if (groupId == 0) {
	        // 私人活動：固定指派給自己
	        assignedUserIds.add(req.getCreatedBy());
	    } else {
	        // 群組活動：使用前端多選成員
	        if (req.getAssignedUserIds() != null && !req.getAssignedUserIds().isEmpty()) {
	            assignedUserIds.addAll(req.getAssignedUserIds());
	        }

	        // 舊版 fallback
	        if (assignedUserIds.isEmpty() && req.getAssignedUserId() != null) {
	            assignedUserIds.add(req.getAssignedUserId());
	        }

	        if (assignedUserIds.isEmpty()) {
	            return new CalendarRes(400, "請選擇指派成員");
	        }
	    }

	    assignedUserIds = assignedUserIds.stream().distinct().toList();

	    // =========================
	    // 4. 檢查群組成員是否真的在群組內
	    // =========================
	    if (groupId != 0) {

	        List<Long> validAssignedUserIds = new ArrayList<>();

	        for (Long assignedUserId : assignedUserIds) {

	            int memberCount = groupMemberDao.countByGroupIdAndUserId(
	                    groupId,
	                    assignedUserId
	            );

	            // 還在群組內的成員才保留
	            if (memberCount > 0) {
	                validAssignedUserIds.add(assignedUserId);
	            }
	        }

	        // 全部指派成員都已經不在群組內
	        if (validAssignedUserIds.isEmpty()) {
	            return new CalendarRes(400, "此活動的指派成員已不在群組內，請重新指派成員");
	        }

	        assignedUserIds = validAssignedUserIds;
	    }
	    
	    List<Long> oldAssignedUserIds =
	            calendarDao.findAssignedUserIdsByEventBatchId(eventBatchId);

	    List<Long> oldSorted = oldAssignedUserIds.stream()
	            .sorted()
	            .toList();

	    List<Long> newSorted = assignedUserIds.stream()
	            .sorted()
	            .toList();

	    boolean sameAssignedUsers = oldSorted.equals(newSorted);

	    int successCount = 0;

	    if (sameAssignedUsers) {
	        successCount = calendarDao.updateCalendarEventBatch(
	                eventBatchId,
	                req.getCreatedBy(),
	                req.getTitle(),
	                req.getDescription(),
	                req.getEventTime(),
	                req.getEndTime(),
	                req.getNotifyBefore()
	        );
	    } else {
	        calendarDao.deleteByEventBatchId(eventBatchId);

	        LocalDateTime taiwanNow = LocalDateTime.now(TAIWAN_ZONE);

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
	                    req.getNotifyBefore(),
	                    taiwanNow
	            );

	            successCount += result;
	        }
	    }

	    if (successCount <= 0) {
	        return new CalendarRes(500, "修改失敗");
	    }

	   
	    // =========================
	    // 7. 發送通知
	    // =========================
	    if (groupId != 0) {

	        String content = groupDao.getSelfName(req.getCreatedBy())
	                + "修改行事曆活動：" + oldCalendarTitle + " → " + req.getTitle();

	        LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

	        List<EmailNotifyUserDTO> notifyUsers =
	                userInfoDao.findEmailNotifyUsers(assignedUserIds);

	        Set<Long> pushUserIds = new HashSet<>();

	        for (Long assignedUserId : assignedUserIds) {
	            calendarDao.insertCalendarEventNotify(
	                    groupId,
	                    assignedUserId,
	                    content,
	                    "update",
	                    false,
	                    now
	            );

	            pushUserIds.add(assignedUserId);
	        }

	        for (EmailNotifyUserDTO user : notifyUsers) {
	            if (Boolean.TRUE.equals(user.getNotifyByEmail())) {
	                emailService.sendMailAsync(
	                        user.getEmail(),
	                        "群組通知",
	                        content
	                );
	            }
	        }

	        for (Long pushUserId : pushUserIds) {
	            int unreadCount = notifyDao.countUnreadByUserId(pushUserId);
	            notifySocketService.pushUnreadCount(pushUserId, unreadCount);
	        }
	    }

	    return new CalendarRes(200, "修改成功");}
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

		    List<groupMembersDTO> getGroupMembers =
		            groupMemberDao.getMembersByGroupId(groupId);

		    List<Long> notifyUserIds = getGroupMembers.stream()
		            .map(groupMembersDTO::getUser_id)
		            .filter(memberId -> !java.util.Objects.equals(memberId, userId))
		            .distinct()
		            .toList();

		    if (!notifyUserIds.isEmpty()) {

		        LocalDateTime now = LocalDateTime.now(TAIWAN_ZONE);

		        List<EmailNotifyUserDTO> notifyUsers =
		                userInfoDao.findEmailNotifyUsers(notifyUserIds);

		        for (Long notifyUserId : notifyUserIds) {
		            calendarDao.insertCalendarEventNotify(
		                    groupId,
		                    notifyUserId,
		                    content,
		                    "update",
		                    false,
		                    now
		            );
		        }

		        for (EmailNotifyUserDTO user : notifyUsers) {
		            if (Boolean.TRUE.equals(user.getNotifyByEmail())) {
		                emailService.sendMailAsync(
		                        user.getEmail(),
		                        "群組通知",
		                        content
		                );
		            }
		        }

		        for (Long notifyUserId : notifyUserIds) {
		            int unreadCount = notifyDao.countUnreadByUserId(notifyUserId);
		            notifySocketService.pushUnreadCount(notifyUserId, unreadCount);
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
			list = calendarDao.findByGroupIdAndAssignedUserIdOrderByEventTimeAsc(realGroupId, userId);
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

		List<Calendar> list = calendarDao.findByGroupIdAndAssignedUserIdOrderByEventTimeAsc(groupId, userId);

		return new CalendarRes(200, "查詢成功", list);
	}

	// 查詢同一批活動目前指派的成員 ID
	public CalendarRes getBatchAssignedUsers(String eventBatchId) {

		if (eventBatchId == null || eventBatchId.isBlank()) {
			return new CalendarRes(400, "eventBatchId 不可為空");
		}

		List<Long> assignedUserIds = calendarDao.findAssignedUserIdsByEventBatchId(eventBatchId);

		return new CalendarRes(200, "查詢成功", assignedUserIds);
	}

}
