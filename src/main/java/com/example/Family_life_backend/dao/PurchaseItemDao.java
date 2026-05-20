package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.Family_life_backend.entity.PurchaseItem;

import jakarta.transaction.Transactional;

public interface PurchaseItemDao extends JpaRepository<PurchaseItem, Integer> {

	/* 新增購物項目 */
	@Modifying
	@Transactional
	@Query(value = "insert into shopping_list_items (id, shopping_list_id, created_by_id,"
			+ " created_at, user_id, category_id, item_name, quantity)"
			+ "values (?1, ?2, ?3, ?4, ?5, ?6, ?7, ?8)", nativeQuery = true)
	public void insert(int id, int listId, int createrId, LocalDate createdDate, int userId, //
			int categoryId, String item, int quantity, boolean check, LocalDate checkDate, int checkMan);

	/* 查看購物項目 */
	@Query(value = "select * from shopping_list_items where shopping_list_id = ?1", nativeQuery = true)
	public List<PurchaseItem> getByListId(int listId);
	
	/* 刪除購物項目(現尚未購買) */
	@Modifying
	@Transactional
	@Query(value = "delete from shopping_list_items where shopping_list_id in (?)", nativeQuery = true)
	public void delete(List<Integer> quizIds);

	

}
