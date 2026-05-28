package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.WarrantyDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.request.AddWarrantyReq;
import com.example.Family_life_backend.request.UpdateWarrantyReq;
import com.example.Family_life_backend.response.MedicineRes;
import com.example.Family_life_backend.response.WarrantyRes;

@Service
public class WarrantyService {

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

	public WarrantyRes getByGroup(Integer groupId, Integer userId) {

	    if (userId == null || userId <= 0) {
	        return new WarrantyRes(400, "userId 不可為空");
	    }

	    return new WarrantyRes(
	            200,
	            "查詢成功",
	            warrantyDao.findByGroupId(userId, groupId)
	    );
	}

	public WarrantyRes add(AddWarrantyReq req) {

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

		warrantyDao.addWarranty(req.getGroupId(), req.getUserId(), req.getProductName(), req.getBrand(), req.getModel(),
				req.getSerialNumber(), req.getPurchaseDate(), req.getWarrantyEndDate(), req.getStoreName(),
				req.getPrice() != null ? req.getPrice() : 0, req.getNotify() != null ? req.getNotify() : true,
				req.getNote(), status, remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
		String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getProductName() + "到保固清單";

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
		return new WarrantyRes(200, "新增成功");
	}

	public WarrantyRes update(UpdateWarrantyReq req) {

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
		int result = warrantyDao.updateWarranty(req.getId(), req.getGroupId(), req.getUserId(), req.getProductName(),
				req.getBrand(), req.getModel(), req.getSerialNumber(), req.getPurchaseDate(), req.getWarrantyEndDate(),
				req.getStoreName(), req.getPrice() != null ? req.getPrice() : 0,
				req.getNotify() != null ? req.getNotify() : true, req.getNote(), status, remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
		String content = groupDao.getSelfName((long) req.getUserId()) + "已將保固" + oldName + "改成" + req.getProductName();

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
			return new WarrantyRes(404, "查無此保固資料");
		}

		return new WarrantyRes(200, "修改成功");
	}

	@Transactional
	public WarrantyRes delete(Integer id, Long userId) {

		Long finalGroupId = itemDao.getGroupIdById((long) id);
		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(finalGroupId);
		String content = groupDao.getSelfName(userId) + "已將保固" + itemDao.getItemNameById((long) id) + "刪除";
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
