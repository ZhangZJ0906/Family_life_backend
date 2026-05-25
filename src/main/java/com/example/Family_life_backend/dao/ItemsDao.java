package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.entity.Items;

public interface ItemsDao extends JpaRepository<Items, Long> {
	/* 拿全部 */
	@Query(value = """
			    SELECT * FROM items
			    WHERE (
			        (:groupId IS NULL AND group_id IS NULL AND created_by_id = :userId)
			        OR
			        (:groupId IS NOT NULL AND group_id = :groupId)
			    )
			""", nativeQuery = true)
	public List<Items> getItemByGroupId(@Param("groupId") Integer groupId, @Param("userId") Integer userId);
	
	@Query(value = """
		    SELECT * FROM items
		    WHERE group_id = 0 and created_by_id = :userId
		""", nativeQuery = true)
	public List<Items> getSelfItem(@Param("userId") Integer userId);
	
	@Query(value = "select * from items where id in (:id)", nativeQuery = true)
	public List<Items> getItemById(@Param("id") List<Long> id);

	/* 新增 */
	@Modifying
	@Transactional
	@Query(value = "INSERT INTO items "
	        + "(group_id, category_id, name, quantity, unit, location_id, price, purchase_date, expire_date, notify, note, created_by_id, unit_price, safe_quantity, status, remind_message) "
	        + "VALUES "
	        + "(:groupId, :categoryId, :name, :quantity, :unit, :locationId, :price, :purchaseDate, :expireDate, :notify, :note, :userId, :unitPrice, :safeQuantity, :status, ；remindMessage)",
	        nativeQuery = true)
	int insertItemNative(
	        @Param("groupId") Integer groupId,
	        @Param("categoryId") Integer categoryId,
	        @Param("name") String name,
	        @Param("quantity") Integer quantity,
	        @Param("unit") String unit,
	        @Param("locationId") Long locationId,
	        @Param("price") Integer price,
	        @Param("purchaseDate") LocalDate purchaseDate,
	        @Param("expireDate") LocalDate expireDate,
	        @Param("notify") Boolean notify,
	        @Param("note") String note,
	        @Param("userId") Integer userId,
	        @Param("unitPrice") int unitPrice,
	        @Param("safeQuantity") Integer safeQuantity,
	        @Param("status") String status,
	        @Param("remindMessage") String remindMessage
	);

	/* 更新 */
	@Modifying
	@Transactional
	@Query(value = "UPDATE items SET "
	        + "group_id = :groupId, "
	        + "category_id = :categoryId, "
	        + "name = :name, "
	        + "quantity = :quantity, "
	        + "unit = :unit, "
	        + "location_id = :locationId, "
	        + "price = :price, "
	        + "purchase_date = :purchaseDate, "
	        + "expire_date = :expireDate, "
	        + "notify = :notify, "
	        + "note = :note, "
	        + "unit_price = :unitPrice, "
	        + "safe_quantity = :safeQuantity, "
	        + "status = :status, "
	        + "remind_message = :remindMessage "
	        + "WHERE id = :id",
	        nativeQuery = true)
	int updateItem(
	        @Param("id") int id,
	        @Param("groupId") Integer groupId,
	        @Param("categoryId") Integer categoryId,
	        @Param("name") String name,
	        @Param("quantity") Integer quantity,
	        @Param("unit") String unit,
	        @Param("locationId") Long locationId,
	        @Param("price") Integer price,
	        @Param("purchaseDate") LocalDate purchaseDate,
	        @Param("expireDate") LocalDate expireDate,
	        @Param("notify") Boolean notify,
	        @Param("note") String note,
	        @Param("unitPrice") int unitPrice,
	        @Param("safeQuantity") Integer safeQuantity,
	        @Param("status") String status,
	        @Param("remindMessage") String remindMessage
	);
	/* 刪除 */
	@Modifying
	@Transactional
	@Query(value = "delete from items where id  in (?)", nativeQuery = true)
	public void deleteItemById(List<Integer> id);
}
