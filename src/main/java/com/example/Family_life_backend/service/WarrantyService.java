package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.WarrantyDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.Warranty;
import com.example.Family_life_backend.globalVar.globalVar;
import com.example.Family_life_backend.request.AddWarrantyReq;
import com.example.Family_life_backend.request.UpdateNotifyReq;
import com.example.Family_life_backend.request.UpdateWarrantyReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.WarrantyRes;

@Service
public class WarrantyService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private WarrantyDao warrantyDao;

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

	public WarrantyRes getByGroup(Integer groupId, Integer userId) {

		if (userId == null || userId <= 0) {
			return new WarrantyRes(400, "userId 不可為空");
		}

		return new WarrantyRes(200, "查詢成功", warrantyDao.findByGroupId(userId, groupId));
	}

	public WarrantyRes add(AddWarrantyReq req, MultipartFile image) {

		if (req.getUserId() == null || req.getUserId() <= 0) {
			return new WarrantyRes(400, "userId 不可為空");
		}

		if (req.getProductName() == null || req.getProductName().isBlank()) {
			return new WarrantyRes(400, "產品名稱不可為空");
		}

		if (req.getPurchaseDate() == null) {
			return new WarrantyRes(400, "購買日期不可為空");
		}

		if (req.getWarrantyEndDate() == null) {
			return new WarrantyRes(400, "保固到期日不可為空");
		}

		if (req.getPurchaseDate().isAfter(req.getWarrantyEndDate())) {
			return new WarrantyRes(400, "購買日期不可晚於保固到期日");
		}

		String status = calcWarrantyStatus(req.getWarrantyEndDate());
		String remindMessage = calcWarrantyRemindMessage(req.getWarrantyEndDate());
		// 圖片上傳
		String avatarUrl = itemListNotify.store(image);

		warrantyDao.addWarranty(req.getGroupId(), req.getUserId(), req.getProductName(), req.getBrand(), req.getModel(),
				req.getSerialNumber(), req.getPurchaseDate(), req.getWarrantyEndDate(), req.getStoreName(),
				req.getPrice() != null ? req.getPrice() : 0, req.getNotify() != null ? req.getNotify() : true,

				req.getNote(), status, remindMessage, LocalDateTime.now(), avatarUrl);
		// ✅ 通知：私人模式不進來，群組模式非同步處理
		if (req.getGroupId() != 0) {
			String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getProductName() + "到保固清單";
			itemListNotify.notifyGroupMembers(req.getGroupId(), req.getUserId(), content);
		}
		return new WarrantyRes(200, "新增成功");
	}

	public WarrantyRes update(UpdateWarrantyReq req, MultipartFile image) {

		String oldName = warrantyDao.getNameById(req.getId());

		if (req.getProductName() == null || req.getProductName().isBlank()) {
			return new WarrantyRes(400, "產品名稱不可為空");
		}

		if (req.getPurchaseDate() == null) {
			return new WarrantyRes(400, "購買日期不可為空");
		}

		if (req.getWarrantyEndDate() == null) {
			return new WarrantyRes(400, "保固到期日不可為空");
		}

		if (req.getPurchaseDate().isAfter(req.getWarrantyEndDate())) {
			return new WarrantyRes(400, "購買日期不可晚於保固到期日");
		}

		String status = calcWarrantyStatus(req.getWarrantyEndDate());
		String remindMessage = calcWarrantyRemindMessage(req.getWarrantyEndDate());

		String oldAvatarString = warrantyDao.getWarrantyImage(Long.valueOf(req.getId()));
		String avatarUrl = oldAvatarString;
		// 💡 修正點 1：先檢查 image 是否存在且不為空，才進行圖片儲存邏輯
		if (image != null && !image.isEmpty()) {
			// 圖片上傳
			avatarUrl = itemListNotify.store(image);
		}
		int result = warrantyDao.updateWarranty(req.getId(), req.getGroupId(), req.getUserId(), req.getProductName(),
				req.getBrand(), req.getModel(), req.getSerialNumber(), req.getPurchaseDate(), req.getWarrantyEndDate(),
				req.getStoreName(), req.getPrice() != null ? req.getPrice() : 0,

				req.getNotify() != null ? req.getNotify() : true, req.getNote(), status, remindMessage,
				LocalDateTime.now(), avatarUrl);
		if (result == 0) {
			return new WarrantyRes(404, "查無此保固資料");
		}

		// ✅ 通知：私人模式不進來，群組模式非同步處理
		if (req.getGroupId() != 0) {
			String content = groupDao.getSelfName((long) req.getUserId()) + "已將保固「" + oldName + "」改成「"
					+ req.getProductName() + "」";
			itemListNotify.notifyGroupMembers(req.getGroupId(), req.getUserId(), content);
		}
		return new WarrantyRes(200, "修改成功");
	}

	// 更新notify
	public BasicRes updateNotify(UpdateNotifyReq req) {
		warrantyDao.updateNotifyById(req.getId(), req.getNotify());
		return new BasicRes("成功", 200);
	}

	@Transactional
	public WarrantyRes delete(Integer id, Long userId) {
		int finalGroupId = 0;
		List<Warranty> warranty = warrantyDao.findByGroupId(id);
		if (warranty != null && !warranty.isEmpty()) {
			finalGroupId = warranty.get(0).getGroupId(); // 拿第一筆的 groupId
		}

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) finalGroupId);
		String content = groupDao.getSelfName(userId) + "已將保固" + warrantyDao.getNameById(id) + "刪除";
		if (finalGroupId != 0) {
			itemListNotify.notifyGroupMembers(Math.toIntExact(finalGroupId), userId, content);
		}

		int result = warrantyDao.deleteWarranty(id);

		if (result == 0) {
			return new WarrantyRes(404, "查無此保固資料");
		}

		return new WarrantyRes(200, "刪除成功");
	}

	private String calcWarrantyStatus(LocalDate warrantyEndDate) {
		LocalDate today = LocalDate.now();

		if (warrantyEndDate != null && warrantyEndDate.isBefore(today)) {
			return "已過保";
		}

		if (warrantyEndDate != null && !warrantyEndDate.isAfter(today.plusDays(30))) {
			return "即將到期";
		}

		return "正常";
	}

	private String calcWarrantyRemindMessage(LocalDate warrantyEndDate) {
		LocalDate today = LocalDate.now();

		if (warrantyEndDate == null) {
			return "";
		}

		long daysLeft = ChronoUnit.DAYS.between(today, warrantyEndDate);

		if (daysLeft < 0) {
			return "已過保 " + Math.abs(daysLeft) + " 天";
		}

		if (daysLeft <= 30) {
			return "保固剩餘 " + daysLeft + " 天";
		}

		return "";
	}
}
