package com.example.Family_life_backend.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Family_life_backend.entity.UserInfo;

import jakarta.transaction.Transactional;

@Repository
public interface UserInfoDao extends JpaRepository<UserInfo, Integer> {

	/* 註冊 */
	@Modifying
	@Transactional
	@Query(value = "insert ignore into users( name, email, password, avatar, is_notify, created_at)"//
			+ "values (?1, ?2, ?3, ?4, ?5, ?6)", nativeQuery = true)
	public void insert(String email, String userName, String pwd, //
			String avatar, boolean notify, String createdDate);

	/* 檢查 Email 是否存在 */
	public boolean existsByEmail(String email);

	/* 登入用 Email 查詢 */
	@Query(value = "select * from users where email = ?1", nativeQuery = true)
	public UserInfo getByEmail(String email);

	/* 更改密碼 */
	@Modifying
	@Transactional
	@Query(value = "update users set password = ?2, updated_at = ?3 where user_id = ?1", nativeQuery = true)
	public void updatePwd(int userId, String newPwd, String updateDate);

	/* 變更資料 */
	@Modifying
	@Transactional
	@Query(value = "update users set name = ?2, email = ?3, avatar = ?4, is_notify_by_enddate = ?5, is_notify_by_email = ?6, updated_at = ?7 where user_id = ?1", nativeQuery = true)
	public void updateInfo(int userId, String userName, String email, String avatar, boolean NotifyByEndDate, boolean NotifyByEmail, String updateDate);
	
	//拿個資
	@Query(value = "select * from users where user_id = :userId", nativeQuery = true)
	public UserInfo getSelfInfoById(@Param("userId")Long userId);
	
}
