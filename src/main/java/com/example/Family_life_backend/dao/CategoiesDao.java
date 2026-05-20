package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.entity.Categories;

@Repository
public interface CategoiesDao extends JpaRepository<Categories, Integer> {

	@Query(value = "SELECT * FROM categories ", nativeQuery = true)
	public List<Categories> getItemCategoriesList();
}
