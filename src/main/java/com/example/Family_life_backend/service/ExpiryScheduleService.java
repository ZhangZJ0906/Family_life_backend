package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.MedicineDao;
import com.example.Family_life_backend.dao.SubscriptionDao;
import com.example.Family_life_backend.dao.WarrantyDao;
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
    private SubscriptionDao subscriptionDao;

    // 每天凌晨 00:00 自動執行
    @Scheduled(fixedRate = 100000)
    public void updateAllStatusDaily() {
        updateItemStatus();
        updateMedicineStatus();
        updateWarrantyStatus();
        updateSubscriptionStatus();

        System.out.println("所有到期狀態更新完成");
    }

    // 測試用：每 10 秒跑一次
    // @Scheduled(fixedRate = 10000)
    // public void testUpdateAllStatus() {
    //     updateAllStatusDaily();
    // }

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
                } else if (
                    item.getQuantity() != null &&
                    item.getSafeQuantity() != null &&
                    item.getQuantity() <= item.getSafeQuantity()
                ) {
                    status = "庫存不足";
                    remindMessage = "目前庫存低於安全庫存";
                } else if (daysLeft <= 7) {
                    status = "即將到期";
                    remindMessage = "剩餘 " + daysLeft + " 天";
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
                } else if (
                    med.getQuantity() != null &&
                    med.getSafeQuantity() != null &&
                    med.getQuantity() <= med.getSafeQuantity()
                ) {
                    status = "庫存不足";
                    remindMessage = "目前藥品低於安全庫存";
                } else if (daysLeft <= 30) {
                    status = "即將到期";
                    remindMessage = "剩餘 " + daysLeft + " 天";
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
                } else if (daysLeft <= 30) {
                    status = "即將到期";
                    remindMessage = "保固剩餘 " + daysLeft + " 天";
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

                if (daysLeft <= 3) {
                    status = "試用即將結束";
                    remindMessage = "試用剩餘 " + daysLeft + " 天";
                } else {
                    status = "試用中";
                    remindMessage = "";
                }

            } else if (sub.getNextBillingDate() != null) {
                long daysLeft = ChronoUnit.DAYS.between(today, sub.getNextBillingDate());

                if (daysLeft < 0) {
                    status = "已逾期扣款";
                    remindMessage = "扣款日已過 " + Math.abs(daysLeft) + " 天";
                } else if (daysLeft <= 3) {
                    status = "即將扣款";
                    remindMessage = "距離扣款剩餘 " + daysLeft + " 天";
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