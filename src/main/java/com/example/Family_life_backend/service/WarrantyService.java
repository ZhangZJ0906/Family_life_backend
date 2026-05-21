package com.example.Family_life_backend.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.WarrantyDao;
import com.example.Family_life_backend.request.AddWarrantyReq;
import com.example.Family_life_backend.request.UpdateWarrantyReq;
import com.example.Family_life_backend.response.WarrantyRes;

@Service
public class WarrantyService {

    @Autowired
    private WarrantyDao warrantyDao;

    public WarrantyRes getByGroup(Integer groupId) {
        if (groupId == null || groupId <= 0) {
            return new WarrantyRes(400, "groupId 不可為空");
        }

        return new WarrantyRes(
                200,
                "查詢成功",
                warrantyDao.findByGroupId(groupId)
        );
    }

    public WarrantyRes add(AddWarrantyReq req) {
        if (req.getGroupId() == null || req.getGroupId() <= 0) {
            return new WarrantyRes(400, "groupId 不可為空");
        }

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

        warrantyDao.addWarranty(
                req.getGroupId(),
                req.getUserId(),
                req.getProductName(),
                req.getBrand(),
                req.getModel(),
                req.getSerialNumber(),
                req.getPurchaseDate(),
                req.getWarrantyEndDate(),
                req.getStoreName(),
                req.getPrice() != null ? req.getPrice() : 0,
                req.getNotify() != null ? req.getNotify() : true,
                req.getNote(),
                status
        );

        return new WarrantyRes(200, "新增成功");
    }

    public WarrantyRes update(UpdateWarrantyReq req) {
        if (req.getId() == null || req.getId() <= 0) {
            return new WarrantyRes(400, "id 不可為空");
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

        int result = warrantyDao.updateWarranty(
                req.getId(),
                req.getGroupId(),
                req.getUserId(),
                req.getProductName(),
                req.getBrand(),
                req.getModel(),
                req.getSerialNumber(),
                req.getPurchaseDate(),
                req.getWarrantyEndDate(),
                req.getStoreName(),
                req.getPrice() != null ? req.getPrice() : 0,
                req.getNotify() != null ? req.getNotify() : true,
                req.getNote(),
                status
        );

        if (result == 0) {
            return new WarrantyRes(404, "查無此保固資料");
        }

        return new WarrantyRes(200, "修改成功");
    }

    public WarrantyRes delete(Integer id) {
        if (id == null || id <= 0) {
            return new WarrantyRes(400, "id 不可為空");
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
    }
