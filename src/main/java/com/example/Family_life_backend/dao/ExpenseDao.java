package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.entity.Expense;

import jakarta.transaction.Transactional;

@Repository
public interface ExpenseDao extends JpaRepository<Expense, Integer> {
	@Query(value = """
			    SELECT * FROM expenses
			    WHERE (:groupId IS NULL OR group_id = :groupId)
			    AND (:userId IS NULL OR user_id = :userId)
			""", nativeQuery = true)
	public List<Expense> findExpenses(@Param("groupId") Long groupId, @Param("userId") Long userId);

	@Query(value = "Select * from expenses where user_id = :userId and group_id =0", nativeQuery = true)
	public List<Expense> findPersonalExpenses(@Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query(value = "INSERT ignore INTO expenses (group_id,user_id, price, category_id, related_item_id, expense_date, note, created_at,related_item_name) "
			+ "VALUES (:groupId,:userId, :price, :categoryId, :relatedItemId, :expenseDate, :note, NOW(),:relatedItemName)", nativeQuery = true)
	public void insertExpense(@Param("groupId") Long groupId, @Param("userId") Long userId,
			@Param("price") Integer price, @Param("categoryId") Integer categoryId,
			@Param("relatedItemId") Long relatedItemId, @Param("relatedItemName") String relatedItemName,
			@Param("expenseDate") LocalDate expenseDate, @Param("note") String note);

	@Modifying
	@Transactional
	@Query(value = "UPDATE expenses SET " + "group_id = :groupId, " + "user_id = :userId, " + "price = :price, "
			+ "category_id = :categoryId, " + "related_item_id = :relatedItemId, related_item_name= :relatedItemName, "
			+ "expense_date = :expenseDate, " + "note = :note  " + "WHERE id = :id", nativeQuery = true)
	public void updateExpense(@Param("id") Integer id, @Param("groupId") Long groupId, @Param("userId") Long userId,
			@Param("price") Integer price, @Param("categoryId") Integer categoryId,
			@Param("relatedItemId") Long relatedItemId, @Param("relatedItemName") String relatedItemName,
			@Param("expenseDate") LocalDate expenseDate, @Param("note") String note);

	@Modifying
	@Transactional
	@Query(value = "delete from expenses   where id in (:id) ", nativeQuery = true)
	public void deleteExpense(@Param("id") List<Integer> id);

	// 通知 2026-05-27 by ZJ
	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (send_id, get_user_id, content, type, is_read)
			    values (:sendId, :getUserId, :content, :type, :isRead)
			""", nativeQuery = true)
	public void insertExpensesEventNotify(@Param("sendId") Long sendId, @Param("getUserId") Long getUserId,
			@Param("content") String content, @Param("type") String type, @Param("isRead") boolean isRead);

}
