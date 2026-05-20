package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Family_life_backend.entity.Subscription;

@Repository
public interface SubscriptionDao extends JpaRepository<Subscription, Integer> {

    // 依群組查詢訂閱
    @Query(value = "SELECT * FROM subscriptions WHERE group_id = :groupId ORDER BY next_billing_date ASC", nativeQuery = true)
    List<Subscription> findByGroupId(@Param("groupId") Integer groupId);

    // 新增訂閱
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO subscriptions "
            + "(group_id, user_id, name, price, billing_cycle, next_billing_date, purchase_date, trial_end_date, notify, note, created_at) "
            + "VALUES "
            + "(:groupId, :userId, :name, :price, :billingCycle, :nextBillingDate, :purchaseDate, :trialEndDate, :notify, :note, NOW())",
            nativeQuery = true)
    int addSubscription(
            @Param("groupId") Integer groupId,
            @Param("userId") Integer userId,
            @Param("name") String name,
            @Param("price") Integer price,
            @Param("billingCycle") String billingCycle,
            @Param("nextBillingDate") LocalDate nextBillingDate,
            @Param("purchaseDate") LocalDate purchaseDate,
            @Param("trialEndDate") LocalDate trialEndDate,
            @Param("notify") Boolean notify,
            @Param("note") String note
    );

    // 修改訂閱
    @Modifying
    @Transactional
    @Query(value = "UPDATE subscriptions SET "
            + "group_id = :groupId, "
            + "user_id = :userId, "
            + "name = :name, "
            + "price = :price, "
            + "billing_cycle = :billingCycle, "
            + "next_billing_date = :nextBillingDate, "
            + "purchase_date = :purchaseDate, "
            + "trial_end_date = :trialEndDate, "
            + "notify = :notify, "
            + "note = :note "
            + "WHERE id = :id",
            nativeQuery = true)
    int updateSubscription(
            @Param("id") Integer id,
            @Param("groupId") Integer groupId,
            @Param("userId") Integer userId,
            @Param("name") String name,
            @Param("price") Integer price,
            @Param("billingCycle") String billingCycle,
            @Param("nextBillingDate") LocalDate nextBillingDate,
            @Param("purchaseDate") LocalDate purchaseDate,
            @Param("trialEndDate") LocalDate trialEndDate,
            @Param("notify") Boolean notify,
            @Param("note") String note
    );

    // 刪除訂閱
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM subscriptions WHERE id = :id", nativeQuery = true)
    int deleteSubscription(@Param("id") Integer id);
}
