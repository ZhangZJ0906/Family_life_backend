package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Family_life_backend.enity.Items;

public interface ItemsDao extends JpaRepository<Items, Long> {

	@Query(value = "select * from items where group_id = ?", nativeQuery = true)
	public List<Items> getItemByGroupId(int groupId);

	@Query(value = "SELECT * FROM locations ", nativeQuery = true)
	public List<Object[]> getItemLocationList();

	@Query(value = "SELECT * FROM categories ", nativeQuery = true)
	public List<Object[]> getItemCategoriesList();

}
