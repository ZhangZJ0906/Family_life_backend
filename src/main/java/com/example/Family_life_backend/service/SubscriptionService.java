package com.example.Family_life_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.SubscriptionDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.request.AddSubscriptionReq;
import com.example.Family_life_backend.request.UpdateSubscriptionReq;
import com.example.Family_life_backend.response.SubscriptionRes;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.example.Family_life_backend.entity.Subscription;
import com.example.Family_life_backend.vo.SubscriptionVo;

@Service
public class SubscriptionService {

	@Autowired
	private SubscriptionDao subscriptionDao;

	@Autowired
	private ItemsDao itemDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	// 查詢
	public SubscriptionRes getByGroup(Integer groupId, Integer userId) {

	    if (userId == null || userId <= 0) {
	        return new SubscriptionRes(400, "userId 不可為空");
	    }

	    List<Subscription> subscriptionList =
	            subscriptionDao.findByGroupId(userId, groupId);

	    List<SubscriptionVo> resultList = new ArrayList<>();

	    for (Subscription sub : subscriptionList) {
	        SubscriptionVo vo = new SubscriptionVo(
	                sub.getId(),
	                sub.getGroupId(),
	                sub.getUserId(),
	                sub.getName(),
	                sub.getPrice(),
	                sub.getBillingCycle(),
	                sub.getPurchaseDate(),
	                sub.getTrialEndDate(),
	                sub.getNextBillingDate(),
	                sub.getStatus(),
	                sub.getRemindMessage(),
	                sub.getNotify() == null ? true : sub.getNotify(),
	                sub.getNote()
	        );

	        resultList.add(vo);
	    }

	    return new SubscriptionRes(200, "查詢成功", resultList);
	}

	// 新增
	public SubscriptionRes add(AddSubscriptionReq req) {

		if (req.getName() == null || req.getName().isBlank()) {
			return new SubscriptionRes(400, "訂閱名稱不可為空");
		}

		if (req.getPrice() == null || req.getPrice() < 0) {
			return new SubscriptionRes(400, "價格不可小於 0");
		}

		if (req.getBillingCycle() == null || req.getBillingCycle().isBlank()) {
			return new SubscriptionRes(400, "扣款週期不可為空");
		}

		LocalDate nextBillingDate = calculateNextBillingDate(req.getTrialEndDate(), req.getBillingCycle());

		if (nextBillingDate == null) {
			return new SubscriptionRes(400, "試用結束日不可為空");
		}

		String status = getSubscriptionStatus(req.getTrialEndDate(), nextBillingDate);

		String remindMessage = getSubscriptionRemindMessage(req.getTrialEndDate(), nextBillingDate);
		subscriptionDao.addSubscription(req.getGroupId(), req.getUserId(), req.getName(), req.getPrice(),
				req.getBillingCycle(), nextBillingDate, req.getPurchaseDate(), req.getTrialEndDate(),
				req.getNotify() == null ? true : req.getNotify(), req.getNote(), status, remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
		String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getName() + "到訂閱清單";

		if (req.getGroupId() != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) req.getGroupId(), member.getUser_id(), content, "itemlist",
							false);
					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}
		}

		return new SubscriptionRes(200, "新增成功");
	}

	// 修改
	public SubscriptionRes update(UpdateSubscriptionReq req) {

		String oldName = subscriptionDao.getOldNameById(req.getId());

		LocalDate nextBillingDate = calculateNextBillingDate(req.getTrialEndDate(), req.getBillingCycle());

		if (nextBillingDate == null) {
			return new SubscriptionRes(400, "試用結束日不可為空");
		}

		String status = getSubscriptionStatus(req.getTrialEndDate(), nextBillingDate);

		String remindMessage = getSubscriptionRemindMessage(req.getTrialEndDate(), nextBillingDate);

		System.out.println(req.getPrice());
		int result = subscriptionDao.updateSubscription(req.getId(), req.getGroupId(), req.getUserId(), req.getName(),
				req.getPrice(), req.getBillingCycle(), nextBillingDate, req.getPurchaseDate(), req.getTrialEndDate(),
				req.getNotify() == null ? true : req.getNotify(), req.getNote(), status, remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
		String content = groupDao.getSelfName((long) req.getUserId()) + "已將訂閱" + oldName + "改成" + req.getName();

		if (req.getGroupId() != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) req.getGroupId(), member.getUser_id(), content, "update", false);
					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}
		}

		if (result == 0) {
			return new SubscriptionRes(404, "查無此訂閱資料");
		}

		return new SubscriptionRes(200, "修改成功");
	}

	// 刪除訂閱
	public SubscriptionRes delete(Integer id, Long userId) {
		if (id == null || id <= 0) {
			return new SubscriptionRes(400, "id 不可為空");
		}

		Long finalGroupId = subscriptionDao.getGroupId(id);
		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(finalGroupId);
		String content = groupDao.getSelfName(userId) + "已將訂閱" + subscriptionDao.getOldNameById(id) + "刪除";
		if (finalGroupId != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != userId) {
					itemDao.addGroupItemNotify((long) finalGroupId, member.getUser_id(), content, "update", false);
					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}
		}

		int result = subscriptionDao.deleteSubscription(id);

		if (result == 0) {
			return new SubscriptionRes(404, "查無此訂閱資料");
		}

		return new SubscriptionRes(200, "刪除成功");
	}

	// 判斷訂閱狀態
	private String getSubscriptionStatus(LocalDate trialEndDate, LocalDate nextBillingDate) {
		LocalDate today = LocalDate.now();

		if (trialEndDate != null && !today.isAfter(trialEndDate)) {
			long daysLeft = ChronoUnit.DAYS.between(today, trialEndDate);

			if (daysLeft <= 3) {
				return "試用即將結束";
			}

			return "試用中";
		}

		if (nextBillingDate != null) {
			long daysLeft = ChronoUnit.DAYS.between(today, nextBillingDate);

			if (daysLeft < 0) {
				return "已逾期扣款";
			}

			if (daysLeft <= 3) {
				return "即將扣款";
			}

			return "正常";
		}

		return "未設定";
	}

	// 產生提醒文字
	private String getSubscriptionRemindMessage(LocalDate trialEndDate, LocalDate nextBillingDate) {
		LocalDate today = LocalDate.now();

		if (trialEndDate != null && !today.isAfter(trialEndDate)) {
			long daysLeft = ChronoUnit.DAYS.between(today, trialEndDate);

			if (daysLeft <= 3) {
				return "試用剩餘 " + daysLeft + " 天";
			}

			return "";
		}

		if (nextBillingDate != null) {
			long daysLeft = ChronoUnit.DAYS.between(today, nextBillingDate);

			if (daysLeft < 0) {
				return "扣款日已過 " + Math.abs(daysLeft) + " 天";
			}

			if (daysLeft <= 3) {
				return "距離扣款剩餘 " + daysLeft + " 天";
			}

			return "";
		}

		return "";
	}

	// 依照試用結束日 + 扣款週期，自動計算下次扣款日
	private LocalDate calculateNextBillingDate(LocalDate trialEndDate, String billingCycle) {

		if (trialEndDate == null) {
			return null;
		}

		if (billingCycle == null || billingCycle.isBlank()) {
			return trialEndDate;
		}

		switch (billingCycle) {
		case "每月":
			return trialEndDate.plusMonths(1);

		case "每季":
			return trialEndDate.plusMonths(3);

		case "每半年":
			return trialEndDate.plusMonths(6);

		case "每年":
			return trialEndDate.plusYears(1);

		default:
			return trialEndDate;
		}
	}
}
