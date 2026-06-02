package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.MedicineDao;
import com.example.Family_life_backend.dao.SubscriptionDao;
import com.example.Family_life_backend.dao.WarrantyDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Items;
import com.example.Family_life_backend.entity.Medicine;
import com.example.Family_life_backend.entity.Subscription;
import com.example.Family_life_backend.entity.Warranty;

@Service
public class ExpiryScheduleService {

	@Autowired
	private ItemsDao itemsDao;

	@Autowired
	private MedicineDao medicineDao;

	@Autowired
	private WarrantyDao warrantyDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private SubscriptionDao subscriptionDao;

	List<groupMembersDTO> members = new ArrayList<groupMembersDTO>();

	// 每天 10:00 自動執行
	@Scheduled(cron = "0 0 0 * * ?")
	public void updateAllStatusDaily() {
		updateItemStatus();
		updateMedicineStatus();
		updateWarrantyStatus();
		updateSubscriptionStatus();
		System.out.println("所有到期狀態更新完成");
	}

	public List<groupMembersDTO> getGroupmembers(Long groupId) {
		return groupMemberDao.getMembersByGroupId(groupId);
	}

	// 發送通知
	public void SendWarringNotify(Items item, Medicine med, Warranty war, Subscription sub, String status,
			String remindMessage, List<groupMembersDTO> members) {

		Long groupId = 0L;
		Long userId = 0L;

		// 取得來源資料
		if (item != null) {
			groupId = (long) item.getGroupId();
			userId = (long) item.getCreatedById();

		} else if (med != null) {
			groupId = (long) med.getGroupId();
			userId = (long) med.getUserId();

		} else if (war != null) {
			groupId = (long) war.getGroupId();
			userId = (long) war.getUserId();

		} else if (sub != null) {
			groupId = (long) sub.getGroupId();
			userId = (long) sub.getUserId();

		} else {
			return;
		}

		// 私人項目
		if (groupId == 0L) {

			itemsDao.addGroupItemNotify(userId, userId, remindMessage, "warring_self", false);

			return;
		}

		// 群組項目
		members = getGroupmembers(groupId);

		for (groupMembersDTO member : members) {

			itemsDao.addGroupItemNotify(groupId, member.getUser_id(), remindMessage, "warring", false);
		}
	}

	private void updateItemStatus() {
		List<Items> items = itemsDao.findAll();
		LocalDate today = LocalDate.now();

		for (Items item : items) {
			String status = "正常";
			String remindMessage = "";

			if (item.getExpireDate() != null) {
				long daysLeft = ChronoUnit.DAYS.between(today, item.getExpireDate());

				if (daysLeft < 0) {
					status = "已到期";
					remindMessage = "已過期 " + Math.abs(daysLeft) + " 天";
					if (item.getNotify()) {
						SendWarringNotify(item, null, null, null, status, item.getName() + remindMessage + "，請盡速處理",
								members);
					}

				} else if (item.getQuantity() != null && item.getSafeQuantity() != null
						&& item.getQuantity() <= item.getSafeQuantity()) {
					status = "庫存不足";
					remindMessage = "目前庫存低於安全庫存";

					if (item.getNotify()) {
						SendWarringNotify(item, null, null, null, status, item.getName() + remindMessage + "，請盡速處理",
								members);
					}

				} else if (daysLeft <= 7) {
					status = "即將到期";
					remindMessage = "剩餘 " + daysLeft + " 天";

					if (item.getNotify()) {
						SendWarringNotify(item, null, null, null, status, item.getName() + remindMessage + "，請盡速處理",
								members);
					}

				}
			}

			item.setStatus(status);
			item.setRemindMessage(remindMessage);
		}

		itemsDao.saveAll(items);
	}

	private void updateMedicineStatus() {
		List<Medicine> medicines = medicineDao.findAll();
		LocalDate today = LocalDate.now();

		for (Medicine med : medicines) {
			String status = "正常";
			String remindMessage = "";

			if (med.getExpireDate() != null) {
				long daysLeft = ChronoUnit.DAYS.between(today, med.getExpireDate());

				if (daysLeft < 0) {
					status = "已到期";
					remindMessage = "已過期 " + Math.abs(daysLeft) + " 天";

					if (med.getNotify()) {
						SendWarringNotify(null, med, null, null, status, med.getName() + remindMessage + "，請盡速處理",
								members);
					}

				} else if (med.getQuantity() != null && med.getSafeQuantity() != null
						&& med.getQuantity() <= med.getSafeQuantity()) {
					status = "庫存不足";
					remindMessage = "目前藥品低於安全庫存";

					if (med.getNotify()) {
						SendWarringNotify(null, med, null, null, status, med.getName() + remindMessage + "，請盡速處理",
								members);
					}

				} else if (daysLeft <= 7) {
					status = "即將到期";
					remindMessage = "剩餘 " + daysLeft + " 天";

					if (med.getNotify()) {
						SendWarringNotify(null, med, null, null, status, med.getName() + remindMessage + "，請盡速處理",
								members);
					}
				}
			}

			med.setStatus(status);
			med.setRemindMessage(remindMessage);
		}

		medicineDao.saveAll(medicines);
	}

	private void updateWarrantyStatus() {
		List<Warranty> warranties = warrantyDao.findAll();
		LocalDate today = LocalDate.now();

		for (Warranty warranty : warranties) {
			String status = "正常";
			String remindMessage = "";

			if (warranty.getWarrantyEndDate() != null) {
				long daysLeft = ChronoUnit.DAYS.between(today, warranty.getWarrantyEndDate());

				if (daysLeft < 0) {
					status = "已過保";
					remindMessage = "已過保 " + Math.abs(daysLeft) + " 天";
					if (warranty.getNotify()) {
						SendWarringNotify(null, null, warranty, null, status,
								warranty.getProductName() + remindMessage + "，請盡速處理", members);
					}

				} else if (daysLeft <= 7) {
					status = "即將到期";
					remindMessage = "保固剩餘 " + daysLeft + " 天";
					if (warranty.getNotify()) {
						SendWarringNotify(null, null, warranty, null, status,
								warranty.getProductName() + remindMessage + "，請盡速處理", members);
					}
				}
			}

			warranty.setStatus(status);
			warranty.setRemindMessage(remindMessage);
		}

		warrantyDao.saveAll(warranties);
	}

	private void updateSubscriptionStatus() {
		List<Subscription> subscriptions = subscriptionDao.findAll();
		LocalDate today = LocalDate.now();

		for (Subscription sub : subscriptions) {
			String status = "正常";
			String remindMessage = "";

			if (sub.getTrialEndDate() != null && !today.isAfter(sub.getTrialEndDate())) {
				long daysLeft = ChronoUnit.DAYS.between(today, sub.getTrialEndDate());

				if (daysLeft <= 7) {
					status = "試用即將結束";
					remindMessage = "試用剩餘 " + daysLeft + " 天";
					if (sub.getNotify()) {
						SendWarringNotify(null, null, null, sub, status, sub.getName() + remindMessage, members);
					}

				} else {
					status = "試用中";
					remindMessage = "";
				}

			} else if (sub.getNextBillingDate() != null) {
				long daysLeft = ChronoUnit.DAYS.between(today, sub.getNextBillingDate());

				if (daysLeft < 0) {
					status = "已逾期扣款";
					remindMessage = "扣款日已過 " + Math.abs(daysLeft) + " 天";
					if (sub.getNotify()) {
						SendWarringNotify(null, null, null, sub, status, sub.getName() + remindMessage, members);
					}
				} else if (daysLeft <= 7) {
					status = "即將扣款";
					remindMessage = "距離扣款剩餘 " + daysLeft + " 天";
					if (sub.getNotify()) {
						SendWarringNotify(null, null, null, sub, status, sub.getName() + remindMessage, members);
					}
				} else {
					status = "正常";
					remindMessage = "";
				}
			} else {
				status = "未設定";
				remindMessage = "";
			}

			sub.setStatus(status);
			sub.setRemindMessage(remindMessage);
		}

		subscriptionDao.saveAll(subscriptions);
	}

}