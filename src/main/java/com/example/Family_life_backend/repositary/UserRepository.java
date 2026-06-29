package com.example.Family_life_backend.repositary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.entity.UserInfo;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Long> {
	UserInfo findByEmail(String email);
}
