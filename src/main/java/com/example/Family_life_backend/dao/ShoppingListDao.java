package com.example.Family_life_backend.dao;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.Family_life_backend.entity.ShoppingList;

import jakarta.transaction.Transactional;

public interface ShoppingListDao extends JpaRepository<ShoppingList, Integer> {

	@Modifying
	@Transactional
	@Query(value = "insert into quiz (title, description, start_date, end_date, published)"
			+ "values (?1, ?2, ?3, ?4, ?5)", nativeQuery = true)
	public void insert(String title, String description, LocalDate startDate, LocalDate endDate, boolean pulished);

	
}
