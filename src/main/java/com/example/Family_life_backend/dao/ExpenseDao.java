package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.enity.Expense;

import jakarta.transaction.Transactional;

@Repository
public interface ExpenseDao extends JpaRepository<Expense, Integer> {
	@Query(value = """
			    SELECT * FROM expenses
			    WHERE (:groupId IS NULL OR group_id = :groupId)
			    AND (:userId IS NULL OR user_id = :userId)
			""", nativeQuery = true)
	public List<Expense> findExpenses(@Param("groupId") Long groupId, @Param("userId") Long userId);

	@Modifying
	@Transactional
	@Query(value = "INSERT ignore INTO expenses (group_id,user_id, price, category_id, related_item_id, expense_date, note, created_at) "
			+ "VALUES (:groupId,:userId, :price, :categoryId, :relatedItemId, :expenseDate, :note, NOW())", nativeQuery = true)
	public void insertExpense(@Param("groupId") Long groupId, @Param("userId") Long userId,
			@Param("price") Integer price, @Param("categoryId") Integer categoryId,
			@Param("relatedItemId") Long relatedItemId, @Param("expenseDate") LocalDate expenseDate,
			@Param("note") String note);

	@Modifying
	@Transactional
	@Query(value = "UPDATE expenses SET " + "group_id = :groupId, " + "user_id = :userId, " + "price = :price, "
			+ "category_id = :categoryId, " + "related_item_id = :relatedItemId, " + "expense_date = :expenseDate, "
			+ "note = :note " + "WHERE id = :id", nativeQuery = true)
	public void updateExpense(@Param("id") Integer id, @Param("groupId") Long groupId, @Param("userId") Long userId,
			@Param("price") Integer price, @Param("categoryId") Integer categoryId,
			@Param("relatedItemId") Long relatedItemId, @Param("expenseDate") LocalDate expenseDate,
			@Param("note") String note);

	@Modifying
	@Transactional
	@Query(value = "delete from expenses   where id in (:id) ", nativeQuery = true)
	public void deleteExpense(@Param("id") List<Integer> id);
}
