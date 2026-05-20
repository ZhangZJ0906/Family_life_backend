package com.example.Family_life_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Family_life_backend.entity.ShoppingList;

public interface ShoppingListDao extends JpaRepository<ShoppingList, Integer> {

//	/* 新增清單 */
//	@Modifying
//	@Transactional
//	@Query(value = "insert into shopping_lists (group_id, title, created_by_id, created_at) "
//			+ "values (?1, ?2, ?3, ?4)", nativeQuery = true)
//	public void insert(int groupId, String title, int createrId, LocalDate createdDate);
//	
//	
//	@Modifying
//	@Transactional
//	@Query(value = "delete  from shopping_lists where id = ?1", nativeQuery = true)
//	public void deleteById(int listId);

}
