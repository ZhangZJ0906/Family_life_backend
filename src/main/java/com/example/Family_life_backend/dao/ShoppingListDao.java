package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
<<<<<<< HEAD
import org.springframework.data.repository.query.Param;
=======
>>>>>>> origin/LII

import com.example.Family_life_backend.entity.ShoppingList;

public interface ShoppingListDao extends JpaRepository<ShoppingList, Integer> {

<<<<<<< HEAD
	public List<ShoppingList> findByCreaterId(int createrId);
	
	//拿shoppinList所屬群組
	@Query(value = "select group_id from shopping_lists where id = :listId", nativeQuery = true)
	public Long getgroupIdByListId(@Param("listId") Long listId);
=======
	@Query(value = """
	        SELECT DISTINCT sl.*
	        FROM shopping_lists sl
	        LEFT JOIN group_members gm
	            ON sl.group_id = gm.group_id
	        WHERE
	            (sl.group_id IS NULL AND sl.created_by_id = ?1)
	            OR
	            (sl.group_id = 0 AND sl.created_by_id = ?1)
	            OR
	            (sl.group_id IS NOT NULL AND sl.group_id <> 0 AND gm.user_id = ?1)
	        ORDER BY sl.id DESC
	        """, nativeQuery = true)
	    List<ShoppingList> findVisibleListsByUserId(int userId);
>>>>>>> origin/LII
}
