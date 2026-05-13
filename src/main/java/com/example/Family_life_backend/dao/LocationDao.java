package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.enity.Location;

@Repository
public interface LocationDao {
	@Query(value = "SELECT * FROM locations ", nativeQuery = true)
	public List<Location> getItemLocationList();
}
