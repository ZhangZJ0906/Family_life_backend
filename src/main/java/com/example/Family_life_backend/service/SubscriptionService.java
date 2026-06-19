package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.SubscriptionDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Subscription;
import com.example.Family_life_backend.globalVar.globalVar;
import com.example.Family_life_backend.request.AddSubscriptionReq;
import com.example.Family_life_backend.request.UpdateNotifyReq;
import com.example.Family_life_backend.request.UpdateSubscriptionReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.SubscriptionRes;
import com.example.Family_life_backend.vo.SubscriptionVo;

@Service
public class SubscriptionService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserInfoDao userInfoDao;

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
	@Autowired
	private globalVar globalVar;
	@Autowired
	private ItemListNotifyAndSaveImageService itemListNotify;

	// 查詢
	public SubscriptionRes getByGroup(Integer groupId, Integer userId) {

		// 1. 基礎防呆檢查
		if (userId == null || userId <= 0) {
			return new SubscriptionRes(400, "userId 錯誤");
		}

		List<Subscription> subscriptionList;

		// ================= 情況 A：查私人訂閱 (groupId == 0) =================
		if (groupId == 0) {
			// 直接去查個人訂閱，不需要檢查群組權限
			subscriptionList = subscriptionDao.findBySelfId(Long.valueOf(userId));
		}
		// ================= 情況 B：查群組訂閱 (groupId > 0) =================
		else {
			// 檢查當前使用者是否真的是該群組的成員
			int isMember = groupMemberDao.checkUserIdExistInGroup(Long.valueOf(groupId), Long.valueOf(userId));
			if (isMember <= 0) {
				return new SubscriptionRes(400, "你不是該群組成員");
			}

			// 確利是成員後，才放行撈取該群組的訂閱
			subscriptionList = subscriptionDao.findByGroupId(groupId);
		}
		List<SubscriptionVo> resultList = new ArrayList<>();

		for (Subscription sub : subscriptionList) {
			SubscriptionVo vo = new SubscriptionVo(sub.getId(), sub.getGroupId(), sub.getUserId(), sub.getName(),
					sub.getPrice(), sub.getBillingCycle(), sub.getPurchaseDate(), sub.getTrialEndDate(),
					sub.getNextBillingDate(), sub.getStatus(), sub.getRemindMessage(),
					sub.getNotify() == null ? true : sub.getNotify(), sub.getNote(), sub.getAvatar(),
					sub.getCreatedAt());

			resultList.add(vo);
		}

		return new SubscriptionRes(200, "查詢成功", resultList);
	}

	// 新增
	public SubscriptionRes add(AddSubscriptionReq req, MultipartFile image) {

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
//圖片上傳
		String avatarUrl = null;
		// 💡 修正點 1：先檢查 image 是否存在且不為空，才進行圖片儲存邏輯
		if (image != null && !image.isEmpty()) {
			avatarUrl = itemListNotify.store(image);
		}

		subscriptionDao.addSubscription(req.getGroupId(), req.getUserId(), req.getName(), req.getPrice(),
				req.getBillingCycle(), nextBillingDate, req.getPurchaseDate(), req.getTrialEndDate(),

				req.getNotify() == null ? true : req.getNotify(), req.getNote(), status, remindMessage,
				LocalDateTime.now(), avatarUrl);

		String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getName() + "到訂閱清單";

		if (req.getGroupId() != 0) {
			itemListNotify.notifyGroupMembers(req.getGroupId(), req.getUserId(), content);
		}

		return new SubscriptionRes(200, "新增成功");
	}

	// 修改
	public SubscriptionRes update(UpdateSubscriptionReq req, MultipartFile image) {

		String oldName = subscriptionDao.getOldNameById(req.getId());

		LocalDate nextBillingDate = calculateNextBillingDate(req.getTrialEndDate(), req.getBillingCycle());

		if (nextBillingDate == null) {
			return new SubscriptionRes(400, "試用結束日不可為空");
		}

		String status = getSubscriptionStatus(req.getTrialEndDate(), nextBillingDate);

		String remindMessage = getSubscriptionRemindMessage(req.getTrialEndDate(), nextBillingDate);

		// 圖片更新
		String oldAvatarString = subscriptionDao.getSubscriptionImage(Long.valueOf(req.getId()));
		String avatarUrl = oldAvatarString;
		// 💡 修正點 1：先檢查 image 是否存在且不為空，才進行圖片儲存邏輯
		if (image != null && !image.isEmpty()) {
			avatarUrl = itemListNotify.store(image);
		}
		int result = subscriptionDao.updateSubscription(req.getId(), req.getGroupId(), req.getUserId(), req.getName(),
				req.getPrice(), req.getBillingCycle(), nextBillingDate, req.getPurchaseDate(), req.getTrialEndDate(),

				req.getNotify() == null ? true : req.getNotify(), req.getNote(), status, remindMessage,
				LocalDateTime.now(), avatarUrl);
		if (result == 0) {
			return new SubscriptionRes(404, "查無此訂閱資料");
		}
		String content = groupDao.getSelfName((long) req.getUserId()) + "已將訂閱" + oldName + "改成" + req.getName();

		if (req.getGroupId() != 0) {
			itemListNotify.notifyGroupMembers(req.getGroupId(), req.getUserId(), content);

		}

		return new SubscriptionRes(200, "修改成功");
	}

	// 更新notify
	public BasicRes updateNotify(UpdateNotifyReq req) {
		subscriptionDao.updateNotifyById(req.getId(), req.getNotify());
		return new BasicRes("成功", 200);
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
			itemListNotify.notifyGroupMembers(Math.toIntExact(finalGroupId), userId, content);
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

		// 1. 還在試用期間
		if (trialEndDate != null && !today.isAfter(trialEndDate)) {
			long daysLeft = ChronoUnit.DAYS.between(today, trialEndDate);

			// 試用剩 30 天內
			if (daysLeft <= 30) {
				return "試用即將結束";
			}

			return "試用中";
		}

		// 2. 已過試用期，看下次扣款日
		if (nextBillingDate != null) {
			long daysLeft = ChronoUnit.DAYS.between(today, nextBillingDate);

			// 扣款日已過
			if (daysLeft < 0) {
				return "已逾期扣款";
			}

			// 距離扣款 30 天內
			if (daysLeft <= 30) {
				return "即將扣款";
			}

			return "正常";
		}

		return "未設定";
	}

	// 產生提醒文字
	private String getSubscriptionRemindMessage(LocalDate trialEndDate, LocalDate nextBillingDate) {
		LocalDate today = LocalDate.now();

		// 1. 還在試用期間
		if (trialEndDate != null && !today.isAfter(trialEndDate)) {
			long daysLeft = ChronoUnit.DAYS.between(today, trialEndDate);

			if (daysLeft <= 30) {
				return "試用剩餘 " + daysLeft + " 天";
			}

			return "";
		}

		// 2. 已過試用期，看下次扣款日
		if (nextBillingDate != null) {
			long daysLeft = ChronoUnit.DAYS.between(today, nextBillingDate);

			if (daysLeft < 0) {
				return "扣款日已過 " + Math.abs(daysLeft) + " 天";
			}

			if (daysLeft <= 30) {
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
