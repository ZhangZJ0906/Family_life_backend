package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Family_life_backend.entity.ShoppingList;

public interface ShoppingListDao extends JpaRepository<ShoppingList, Integer> {

	public List<ShoppingList> findByCreaterId(int createrId);

}
