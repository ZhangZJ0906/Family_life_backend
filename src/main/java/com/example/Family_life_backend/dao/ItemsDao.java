package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.enity.Items;

public interface ItemsDao extends JpaRepository<Items, Long> {

	@Query(value = "select * from items where group_id in ?", nativeQuery = true)
	public List<Items> getItemByGroupId(List<Integer> groupId);

	@Modifying
	@Transactional
	@Query(value = "INSERT INTO items (group_id, category_id, name, quantity, unit, location_id, price, expire_date, notify, note, created_by_id) "
			+ "VALUES (:groupId, :catId, :name, :qty, :unit, :locId, :price, :expire, :notify, :note, :userId)", nativeQuery = true)
	void insertItem(@Param("groupId") Integer groupId, @Param("catId") Integer catId, @Param("name") String name,
			@Param("qty") Integer qty, @Param("unit") String unit, @Param("locId") Long locId,
			@Param("price") Integer price, @Param("expire") LocalDate expire, @Param("notify") Boolean notify,
			@Param("note") String note, @Param("userId") Integer userId);



}
