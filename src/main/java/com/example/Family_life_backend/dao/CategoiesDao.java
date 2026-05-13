package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.enity.Categories;

@Repository
public interface CategoiesDao {

	@Query(value = "SELECT * FROM categories ", nativeQuery = true)
	public List<Categories> getItemCategoriesList();
}
