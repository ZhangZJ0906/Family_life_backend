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

	//查詢
    @Query(value = "SELECT * FROM medicines WHERE group_id = :groupId ORDER BY expire_date ASC",
            nativeQuery = true)
    List<Medicine> findByGroupId(@Param("groupId") Integer groupId);

    //新增
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO medicines "
            + "(group_id, user_id, name, medicine_type, quantity, unit, safe_quantity, "
            + "purchase_date, open_date, expire_date, dosage, usage_method, frequency, "
            + "location, source, notify, note, status) "
            + "VALUES "
            + "(:groupId, :userId, :name, :medicineType, :quantity, :unit, :safeQuantity, "
            + ":purchaseDate, :openDate, :expireDate, :dosage, :usageMethod, :frequency, "
            + ":location, :source, :notify, :note, :status)",
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
            @Param("openDate") LocalDate openDate,
            @Param("expireDate") LocalDate expireDate,
            @Param("dosage") String dosage,
            @Param("usageMethod") String usageMethod,
            @Param("frequency") String frequency,
            @Param("location") String location,
            @Param("source") String source,
            @Param("notify") Boolean notify,
            @Param("note") String note,
            @Param("status") String status
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
            + "open_date = :openDate, "
            + "expire_date = :expireDate, "
            + "dosage = :dosage, "
            + "usage_method = :usageMethod, "
            + "frequency = :frequency, "
            + "location = :location, "
            + "source = :source, "
            + "notify = :notify, "
            + "note = :note, "
            + "status = :status "
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
            @Param("openDate") LocalDate openDate,
            @Param("expireDate") LocalDate expireDate,
            @Param("dosage") String dosage,
            @Param("usageMethod") String usageMethod,
            @Param("frequency") String frequency,
            @Param("location") String location,
            @Param("source") String source,
            @Param("notify") Boolean notify,
            @Param("note") String note,
            @Param("status") String status
    );

    //刪除
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM medicines WHERE id = :id", nativeQuery = true)
    int deleteMedicine(@Param("id") Integer id);
}
