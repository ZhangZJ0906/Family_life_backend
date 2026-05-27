package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.MedicineDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.request.AddMedicineReq;
import com.example.Family_life_backend.request.UpdateMedicineReq;
import com.example.Family_life_backend.response.MedicineRes;

@Service
public class MedicineService {

	@Autowired
	private MedicineDao medicineDao;

	@Autowired
	private ItemsDao itemDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private groupDao groupDao;

    public MedicineRes getByGroup(Integer groupId, Integer userId) {


    	    if (userId == null || userId <= 0) {
    	        return new MedicineRes(400, "userId 不可為空");
    	    }

    	    return new MedicineRes(
    	            200,
    	            "查詢成功",
    	            medicineDao.findByGroupId(userId, groupId)
    	    );
    	}
    
	public MedicineRes getByGroup(Integer groupId, Long userId) {

		if (groupId == 0) {
			return new MedicineRes(200, "查詢成功", medicineDao.findBySelfId(userId));
		}

		return new MedicineRes(200, "查詢成功", medicineDao.findByGroupId(groupId));
	}

	public MedicineRes add(AddMedicineReq req) {


		if (req.getUserId() == null || req.getUserId() <= 0) {
			return new MedicineRes(400, "userId 不可為空");
		}

		if (req.getName() == null || req.getName().isBlank()) {
			return new MedicineRes(400, "藥品名稱不可為空");
		}

		if (req.getExpireDate() == null) {
			return new MedicineRes(400, "到期日期不可為空");
		}

		if (req.getPurchaseDate() != null && req.getPurchaseDate().isAfter(req.getExpireDate())) {
			return new MedicineRes(400, "購買日期不可晚於到期日期");
		}

		if (req.getUnit() == null || req.getUnit().isBlank()) {
			return new MedicineRes(400, "單位不可為空");
		}
		Integer quantity = req.getQuantity() != null ? req.getQuantity() : 0;
		Integer safeQuantity = req.getSafeQuantity() != null ? req.getSafeQuantity() : 0;
		String status = calcMedicineStatus(quantity, safeQuantity, req.getExpireDate());
		Integer unitPrice = req.getUnitPrice() != null ? req.getUnitPrice() : 0;
		Integer price = quantity * unitPrice;
		String remindMessage = calcMedicineRemindMessage(quantity, safeQuantity, req.getExpireDate());

		medicineDao.addMedicine(req.getGroupId(), req.getUserId(), req.getName(), req.getMedicineType(), quantity,
				req.getUnit(), safeQuantity, req.getPurchaseDate(), req.getExpireDate(), req.getDosage(),
				req.getUsageMethod(), req.getLocation(), req.getSource(),
				req.getNotify() != null ? req.getNotify() : true, req.getNote(), unitPrice, price, status,
				remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
		String content = groupDao.getSelfName((long) req.getUserId()) + "已新增" + req.getName() + "清單";

		if (req.getGroupId() != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) req.getGroupId(), member.getUser_id(), content, "itemlist",
							false);
				}
			}
		}

		return new MedicineRes(200, "新增成功");
	}

	public MedicineRes update(UpdateMedicineReq req) {

		String oldName = medicineDao.getMedicineNameById(req.getId());

		if (req.getName() == null || req.getName().isBlank()) {
			return new MedicineRes(400, "藥品名稱不可為空");
		}

		if (req.getExpireDate() == null) {
			return new MedicineRes(400, "到期日期不可為空");
		}

		if (req.getPurchaseDate() != null && req.getPurchaseDate().isAfter(req.getExpireDate())) {
			return new MedicineRes(400, "購買日期不可晚於到期日期");
		}

		if (req.getUnit() == null || req.getUnit().isBlank()) {
			return new MedicineRes(400, "單位不可為空");
		}

		Integer quantity = req.getQuantity() != null ? req.getQuantity() : 0;
		Integer safeQuantity = req.getSafeQuantity() != null ? req.getSafeQuantity() : 0;
		String status = calcMedicineStatus(quantity, safeQuantity, req.getExpireDate());
		Integer unitPrice = req.getUnitPrice() != null ? req.getUnitPrice() : 0;
		Integer price = quantity * unitPrice;
		String remindMessage = calcMedicineRemindMessage(quantity, safeQuantity, req.getExpireDate());

		int result = medicineDao.updateMedicine(req.getId(), req.getGroupId(), req.getUserId(), req.getName(),
				req.getMedicineType(), quantity, req.getUnit(), safeQuantity, req.getPurchaseDate(),
				req.getExpireDate(), req.getDosage(), req.getUsageMethod(), req.getLocation(), req.getSource(),
				req.getNotify() != null ? req.getNotify() : true, req.getNote(), unitPrice, price, status,
				remindMessage);

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId((long) req.getGroupId());
		String content = groupDao.getSelfName((long) req.getUserId()) + "已將" + oldName + "清單改成" + req.getName();

		if (req.getGroupId() != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != (long) req.getUserId()) {
					itemDao.addGroupItemNotify((long) req.getGroupId(), member.getUser_id(), content, "update", false);
				}
			}
		}

		if (result == 0) {
			return new MedicineRes(404, "查無此藥品資料");
		}

		return new MedicineRes(200, "修改成功");
	}

	public MedicineRes delete(Integer id, Long userId) {
		if (id == null || id <= 0) {
			return new MedicineRes(400, "id 不可為空");
		}

		Long finalGroupId = medicineDao.getThisGroup(id);
		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(finalGroupId);
		String content = groupDao.getSelfName(userId) + "已將" + medicineDao.getMedicineNameById(id) + "刪除";
		if (finalGroupId != 0) {
			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != userId) {
					itemDao.addGroupItemNotify((long) finalGroupId, member.getUser_id(), content, "update", false);
				}
			}
		}

		int result = medicineDao.deleteMedicine(id);

		if (result == 0) {
			return new MedicineRes(404, "查無此藥品資料");
		}

		return new MedicineRes(200, "刪除成功");
	}

	private String calcMedicineStatus(Integer quantity, Integer safeQuantity, LocalDate expireDate) {
		LocalDate today = LocalDate.now();

		if (expireDate != null && expireDate.isBefore(today)) {
			return "已到期";
		}

		if (quantity != null && safeQuantity != null && quantity <= safeQuantity) {
			return "庫存不足";
		}

		if (expireDate != null && !expireDate.isAfter(today.plusDays(30))) {
			return "即將到期";
		}

		return "正常";
	}

	private String calcMedicineRemindMessage(Integer quantity, Integer safeQuantity, LocalDate expireDate) {
		LocalDate today = LocalDate.now();

		if (expireDate != null) {
			long daysLeft = ChronoUnit.DAYS.between(today, expireDate);

			if (daysLeft < 0) {
				return "已過期 " + Math.abs(daysLeft) + " 天";
			}

			if (daysLeft <= 30) {
				return "剩餘 " + daysLeft + " 天";
			}
		}

		if (quantity != null && safeQuantity != null && quantity <= safeQuantity) {
			return "目前藥品低於安全庫存";
		}

		return "";
	}
}
