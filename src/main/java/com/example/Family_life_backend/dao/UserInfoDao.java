package com.example.Family_life_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.entity.UserInfo;

import jakarta.transaction.Transactional;

@Repository
public interface UserInfoDao extends JpaRepository<UserInfo, Integer> {

	/* 註冊 */
	@Modifying
	@Transactional
	@Query (value = "insert ignore into users( name, email, password, avatar, is_notify, created_at)"//
			+ "values (?1, ?2, ?3, ?4, ?5, ?6)", nativeQuery = true)
	public void insert(String email, String userName, String pwd, //
			String avatar, boolean notify, String createdDate);

	/* 檢查 Email 是否已存在 */
	@Query (value = "select * from users where email = ?1", nativeQuery = true)
	public boolean existsByEmail(String email);

	/* 登入用 Email 找 */
	@Query (value = "select * from users where email = ?1", nativeQuery = true)
	public UserInfo getByEmail(String email);

	
}
