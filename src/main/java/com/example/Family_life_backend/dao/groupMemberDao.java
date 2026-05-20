package com.example.Family_life_backend.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.DTO.UserNotifyDTO;
import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.entity.GroupMembers;
import com.example.Family_life_backend.entity.GroupMembersId;
import com.example.Family_life_backend.entity.invitedMembers;
import com.example.Family_life_backend.entity.notify;
import com.example.Family_life_backend.respond.BasicResponse;

@Repository
public interface groupMemberDao extends JpaRepository<GroupMembers, GroupMembersId> {

	@Modifying
	@Transactional
	@Query(value = """
		    insert into invited_members (user_id, group_id, name, avatar) values (:getUserId, :groupId, :name, :avatar)
		""", nativeQuery = true)
	public void addToInviteMember(@Param("getUserId") Long getUserId,  @Param("groupId") Long groupId, @Param("name") String name, @Param("avatar") String avatar);
	
	@Query(value = "select count(*) from invited_members where user_id = :getUserId and group_id = :groupId", nativeQuery = true)
	public int isInvite(@Param("getUserId") Long getUserId, @Param("groupId") Long groupId);
	
	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (send_id, get_user_id, content, type, is_read, target_group_id)
			    values (:sendUserId, :getUserId, :content, :type, :isRead, :targetGroupId)
			""", nativeQuery = true)
	public void sendInviteNotify(@Param("sendUserId") Long sendUserId, @Param("getUserId") Long getUserId, @Param("content") String content
			, @Param("type") String type, @Param("isRead") boolean isRead, @Param("targetGroupId") Long targretGroupId);
	
	@Modifying
	@Transactional
	@Query(value = """
			    insert into group_members (group_id, user_id, public_inventory)
			    values (:groupId, :userId, :publicInventory)
			""", nativeQuery = true)
	public void insert(@Param("groupId") Long groupId, @Param("userId") Long userId, @Param("publicInventory") int publicInventory);
	
	@Query(value = """
		    select count(*)
		    from group_members
		    where group_id = :groupId
		    and user_id = :userId
		""", nativeQuery = true)
	public int checkUserIdExistInGroup(@Param("groupId") Long groupId, @Param("userId") Long userId);
	
	@Query(value = "select name from users where user_id = :userId", nativeQuery = true)
	public String invitedUserName(@Param("userId") Long userId);
	
	@Query(value = """
		    select count(*)
		    from users
		    where user_id = :userId
		""", nativeQuery = true)
	public int checkUserIdExist(@Param("userId") Long userId);
	
	@Modifying
	@Transactional
	@Query(value = "delete from group_members where group_id = :groupId", nativeQuery = true)
	public void deleteByGroupId(@Param("groupId") Long groupId);

	@Query(value = "select group_id from `groups` where invite_code = :inviteCode", nativeQuery = true)
	public Long findGroupIdByInviteCode(@Param("inviteCode") String inviteCode);
	
	@Modifying
	@Transactional
	@Query(value = """
	    delete from group_members
	    where group_id = ?1
	    and user_id = ?2
	    """, nativeQuery = true)
	public void deleteMember(Long group_id, Long user_id);
	
	@Modifying
	@Transactional
	@Query(value = """
	    delete from invited_members
	    where group_id = ?1
	    and user_id = ?2
	    """, nativeQuery = true)
	public void deleteInvitedMember(Long group_id, Long user_id);
	
	@Query(value = """
		    SELECT
			    n.notify_id AS id,
			    n.send_id AS sendUserId,
			    n.get_user_id AS getUserId,
			    n.content AS content,
			    n.type AS type,
			    n.is_read AS isRead,
			    n.send_date AS sendDate,
			    n.target_group_id AS targetGroupId,
			    n.status AS status,
			    u.name AS name,
			    u.avatar AS avatar
			FROM notify n
			JOIN users u
			    ON n.send_id = u.user_id
			WHERE n.get_user_id = :user_id
			  AND n.type = 'invite'
			
			UNION ALL
			
			SELECT
			    n.notify_id AS id,
			    n.send_id AS sendUserId,
			    n.get_user_id AS getUserId,
			    n.content AS content,
			    n.type AS type,
			    n.is_read AS isRead,
			    n.send_date AS sendDate,
			    n.target_group_id AS targetGroupId,
			    n.status AS status,
			    g.group_name AS name,
			    g.avatar AS avatar
			FROM notify n
			JOIN `groups` g
			    ON n.send_id = g.group_id
			WHERE n.get_user_id = :user_id
			  AND n.type in ('group', 'update')
			
			ORDER BY sendDate DESC;
		    """, nativeQuery = true)
		public List<UserNotifyDTO> getNotifyList(
		    @Param("user_id") Long user_id
		);
	
	@Query(value = "select * from invited_members where group_id = :groupId", nativeQuery = true)
	public List<invitedMembers> getInvitedMemberList(@Param("groupId") Long groupId);

	@Query(value = """
		    SELECT
		        gm.group_id as group_id,
		        gm.user_id as user_id,
		        gm.public_inventory as publicInventory,
		        u.name as user_name,
		        u.avatar as avatar
		    FROM group_members gm
		    JOIN users u
		        ON gm.user_id = u.user_id
		    WHERE gm.group_id = ?1
		    """, nativeQuery = true)
	public List<groupMembersDTO> getMembersByGroupId(Long group_id);
}
