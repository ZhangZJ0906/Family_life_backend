package com.example.Family_life_backend.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Family_life_backend.entity.group;

@Repository
public interface groupDao extends JpaRepository<group, Long> {

	@Modifying
	@Transactional
	@Query(value = "insert into `groups` (group_name, invite_code, created_by, created_at) "
			+ "values(?1, ?2, ?3, ?4)", nativeQuery = true)
	public void create(String group_name, String invite_code, Long createdBy, LocalDateTime createdAt);

	@Query(value = "select name from users where user_id = :userId", nativeQuery = true)
	public String getSelfName(@Param("userId") Long userId);

	@Query(value = "select group_name from `groups` where group_id = :groupId", nativeQuery = true)
	public String getSelfGroupNameById(@Param("groupId") Long groupId);

	@Query(value = """
			    SELECT g.*
			    FROM `groups` g
			    JOIN group_members gm ON g.group_id = gm.group_id
			    WHERE gm.user_id = :userId
			""", nativeQuery = true)
	public List<group> getAll(@Param("userId") Long userId);

	@Query(value = "select group_name from `groups` where group_id = :group_id", nativeQuery = true)
	public String getGroupName(@Param("group_id") Long group_id);

	@Query(value = "select avatar from `groups` where group_id = :groupId", nativeQuery = true)
	String getAvatarByGroupId(@Param("groupId") Long groupId);

	@Modifying
	@Transactional
	@Query(value = "update `groups` set group_name = :groupName, avatar = :Avatar, created_by = :createdBy, creater = :Creater where group_id = :groupId", nativeQuery = true)
	void updateGroup(@Param("groupName") String groupName, @Param("Avatar") String Avatar,
			@Param("createdBy") Long createdBy, @Param("Creater") String Creater, @Param("groupId") Long groupId);

	@Modifying
	@Transactional
	@Query(value = "delete from `groups` where group_id = :groupId", nativeQuery = true)
	void deleteGroup(@Param("groupId") Long groupId);

}
