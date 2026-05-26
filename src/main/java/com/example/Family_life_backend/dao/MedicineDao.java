package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.entity.Medicine;

public interface MedicineDao extends JpaRepository<Medicine, Integer> {

	//查詢群組
	@Query(value = """
		    SELECT *
		    FROM medicines
		    WHERE (
		        (:groupId IS NULL AND group_id IS NULL AND user_id = :userId)
		        OR
		        (:groupId IS NOT NULL AND group_id = :groupId)
		    )
		    ORDER BY expire_date ASC
		""", nativeQuery = true)
		List<Medicine> findByGroupId(
		        @Param("userId") Integer userId,
		        @Param("groupId") Integer groupId
		);

    //新增
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO medicines "
            + "(group_id, user_id, name, medicine_type, quantity, unit, safe_quantity, "
            + "purchase_date, expire_date, dosage, usage_method, "
            + "location, source, notify, note, unit_price, price, status, remind_message) "
            + "VALUES "
            + "(:groupId, :userId, :name, :medicineType, :quantity, :unit, :safeQuantity, "
            + ":purchaseDate, :expireDate, :dosage, :usageMethod, "
            + ":location, :source, :notify, :note, :unitPrice, :price, :status, :remindMessage)",
            nativeQuery = true)
    int addMedicine(
            @Param("groupId") Integer groupId,
            @Param("userId") Integer userId,
            @Param("name") String name,
            @Param("medicineType") String medicineType,
            @Param("quantity") Integer quantity,
            @Param("unit") String unit,
            @Param("safeQuantity") Integer safeQuantity,
            @Param("purchaseDate") LocalDate purchaseDate,
            @Param("expireDate") LocalDate expireDate,
            @Param("dosage") String dosage,
            @Param("usageMethod") String usageMethod,
            @Param("location") String location,
            @Param("source") String source,
            @Param("notify") Boolean notify,
            @Param("note") String note,
            @Param("unitPrice") Integer unitPrice,
            @Param("price") Integer price,
            @Param("status") String status,
            @Param("remindMessage") String remindMessage
    		);
    	

    
    //修改
    @Modifying
    @Transactional
    @Query(value = "UPDATE medicines SET "
            + "group_id = :groupId, "
            + "user_id = :userId, "
            + "name = :name, "
            + "medicine_type = :medicineType, "
            + "quantity = :quantity, "
            + "unit = :unit, "
            + "safe_quantity = :safeQuantity, "
            + "purchase_date = :purchaseDate, "
            + "expire_date = :expireDate, "
            + "dosage = :dosage, "
            + "usage_method = :usageMethod, "
            + "location = :location, "
            + "source = :source, "
            + "notify = :notify, "
            + "note = :note, "
            + "unit_price = :unitPrice, "
            + "price = :price, "
            + "status = :status, "
            + "remind_message = :remindMessage "
            + "WHERE id = :id",
            nativeQuery = true)
    int updateMedicine(
            @Param("id") Integer id,
            @Param("groupId") Integer groupId,
            @Param("userId") Integer userId,
            @Param("name") String name,
            @Param("medicineType") String medicineType,
            @Param("quantity") Integer quantity,
            @Param("unit") String unit,
            @Param("safeQuantity") Integer safeQuantity,
            @Param("purchaseDate") LocalDate purchaseDate,
            @Param("expireDate") LocalDate expireDate,
            @Param("dosage") String dosage,
            @Param("usageMethod") String usageMethod,
            @Param("location") String location,
            @Param("source") String source,
            @Param("notify") Boolean notify,
            @Param("note") String note,
            @Param("unitPrice") Integer unitPrice,
            @Param("price") Integer price,
            @Param("status") String status,
            @Param("remindMessage") String remindMessage
    );
    //刪除
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM medicines WHERE id = :id", nativeQuery = true)
    int deleteMedicine(@Param("id") Integer id);
}
