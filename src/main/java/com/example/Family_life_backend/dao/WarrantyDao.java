package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Family_life_backend.entity.Warranty;

import jakarta.transaction.Transactional;

public interface WarrantyDao extends JpaRepository<Warranty, Integer> {

	//查詢
	@Query(value = """
		    SELECT *
		    FROM warranties
		    WHERE (
		        (:groupId IS NULL AND group_id IS NULL AND user_id = :userId)
		        OR
		        (:groupId IS NOT NULL AND group_id = :groupId)
		    )
		    ORDER BY warranty_end_date ASC
		""", nativeQuery = true)
		List<Warranty> findByGroupId(
		        @Param("userId") Integer userId,
		        @Param("groupId") Integer groupId
		);
	// 查詢
	@Query(value = "SELECT * FROM warranties WHERE group_id = :groupId ORDER BY warranty_end_date ASC", nativeQuery = true)
	List<Warranty> findByGroupId(@Param("groupId") Integer groupId);

	// 查詢自己
	@Query(value = "SELECT * FROM warranties WHERE group_id = 0 and user_id = :userId ORDER BY warranty_end_date  ASC", nativeQuery = true)
	List<Warranty> findBySelfId(@Param("userId") Long userId);

	// 查保固名
	@Query(value = "SELECT product_name FROM warranties WHERE id = :Id", nativeQuery = true)
	String getNameById(@Param("Id") Integer id);

	// 新增
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO warranties " + "(group_id, user_id, product_name, brand, model, serial_number, "
			+ "purchase_date, warranty_end_date, store_name, price, notify, note, status, remind_message) " + "VALUES "
			+ "(:groupId, :userId, :productName, :brand, :model, :serialNumber, "
			+ ":purchaseDate, :warrantyEndDate, :storeName, :price, :notify, :note, :status, :remindMessage)", nativeQuery = true)
	int addWarranty(@Param("groupId") Integer groupId, @Param("userId") Integer userId,
			@Param("productName") String productName, @Param("brand") String brand, @Param("model") String model,
			@Param("serialNumber") String serialNumber, @Param("purchaseDate") LocalDate purchaseDate,
			@Param("warrantyEndDate") LocalDate warrantyEndDate, @Param("storeName") String storeName,
			@Param("price") Integer price, @Param("notify") Boolean notify, @Param("note") String note,
			@Param("status") String status, @Param("remindMessage") String remindMessage);

	// 修改
	@Modifying
	@Transactional
	@Query(value = "UPDATE warranties SET " + "group_id = :groupId, " + "user_id = :userId, "
			+ "product_name = :productName, " + "brand = :brand, " + "model = :model, "
			+ "serial_number = :serialNumber, " + "purchase_date = :purchaseDate, "
			+ "warranty_end_date = :warrantyEndDate, " + "store_name = :storeName, " + "price = :price, "
			+ "notify = :notify, " + "note = :note, " + "status = :status, " + "remind_message = :remindMessage "
			+ "WHERE id = :id", nativeQuery = true)
	int updateWarranty(@Param("id") Integer id, @Param("groupId") Integer groupId, @Param("userId") Integer userId,
			@Param("productName") String productName, @Param("brand") String brand, @Param("model") String model,
			@Param("serialNumber") String serialNumber, @Param("purchaseDate") LocalDate purchaseDate,
			@Param("warrantyEndDate") LocalDate warrantyEndDate, @Param("storeName") String storeName,
			@Param("price") Integer price, @Param("notify") Boolean notify, @Param("note") String note,
			@Param("status") String status, @Param("remindMessage") String remindMessage);

	// 刪除
	@Modifying
	@Transactional
	@Query(value = "DELETE FROM warranties WHERE id = :id", nativeQuery = true)
	int deleteWarranty(@Param("id") Integer id);
}
