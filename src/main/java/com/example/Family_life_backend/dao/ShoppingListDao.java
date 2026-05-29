package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Family_life_backend.entity.ShoppingList;

public interface ShoppingListDao extends JpaRepository<ShoppingList, Integer> {

	public List<ShoppingList> findByCreaterId(int createrId);
	
	//拿shoppinList所屬群組
	@Query(value = "select group_id from shopping_lists where id = :listId", nativeQuery = true)
	public Long getgroupIdByListId(@Param("listId") Long listId);
}
