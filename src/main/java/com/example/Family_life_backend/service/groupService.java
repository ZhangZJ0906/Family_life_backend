package com.example.Family_life_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.EmailNotifyUserDTO;
import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.constant.replyMsg;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.group;
import com.example.Family_life_backend.globalVar.globalVar;
import com.example.Family_life_backend.repositary.GroupRepository;
import com.example.Family_life_backend.request.CreateGroupReq;
import com.example.Family_life_backend.response.BasicResponse;
import com.example.Family_life_backend.response.GetGroupRes;

@Service
public class groupService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private GroupRepository groupRepository;

	@Autowired
	private NotifySocketService notifySocketService;

	@Autowired
	private ItemsDao itemsDao;

	@Autowired
	private NotificationService notifacationService;

	@Autowired
	private globalVar globalVar;

	@Transactional
	public BasicResponse create(CreateGroupReq req) {
		group group = new group();
		String self_name = groupDao.getSelfName(req.getCreateBy());
		group.setGroupName(req.getGroupName());

		String inviteCode;
		do {
			inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
		} while (groupRepository.existsByInviteCode(inviteCode));

		group.setInviteCode(inviteCode);
		group.setCreatedBy(req.getCreateBy());
		group.setCreater(self_name);
		group.setCreatedAt(LocalDateTime.now());
		
		 // 重點：避免 avatar 寫入 null
	    group.setAvatar("/uploads/default-group-avatar.png");

		group saved = groupRepository.save(group);

		groupMemberDao.insert(saved.getGroupId(), saved.getCreatedBy(), 0);

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	public GetGroupRes getList(Long user_id) {
//		System.out.print("groupList: " + groupDao.getMyGroupsPublicInventory(user_id));
//		return new GetGroupRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(), groupDao.getMyGroups(user_id),
//				groupDao.getMyGroupsPublicInventory(user_id));
		try {
			System.out.print("groupList: " + groupDao.getMyGroupsPublicInventory(user_id));

			return new GetGroupRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(),
					groupDao.getMyGroups(user_id), groupDao.getMyGroupsPublicInventory(user_id));

		} catch (Exception e) {
			e.printStackTrace();
			throw e; // 🔥 讓 Spring 顯示真正錯誤，不要假裝成 CORS
		}
	}

	private void pushUnreadForUsers(List<Long> userIds) {

		if (userIds == null || userIds.isEmpty()) {
			return;
		}

		List<Object[]> result = notifyDao.countUnreadByUserIds(userIds);

		Map<Long, Integer> unreadMap = result.stream()
				.collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).intValue()));

		for (Long userId : userIds) {

			notifySocketService.pushUnreadCount(userId, unreadMap.getOrDefault(userId, 0));
		}
	}

	@Transactional
	public BasicResponse updateGroup(Long groupId, String groupName, MultipartFile avatar, Long createdBy) {

		try {

			String selfName = groupDao.getSelfName(createdBy);

			String oldGroupName = groupDao.getSelfGroupNameById(groupId);

			String oldAvatar = groupDao.getAvatarByGroupId(groupId);

			String avatarUrl =
			        oldAvatar == null || oldAvatar.isBlank()
			                ? "/uploads/default-group-avatar.png"
			                : oldAvatar;

			// 更新頭像
			if (avatar != null && !avatar.isEmpty()) {

				String originalName = avatar.getOriginalFilename();

				String ext = ".jpg";

				if (originalName != null && originalName.contains(".")) {

					ext = originalName.substring(originalName.lastIndexOf("."));
				}

				String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;

				Path uploadPath = Paths.get("/app/uploads");

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);

				Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				avatarUrl = "/uploads/" + fileName;
			}

			// 更新群組
			groupDao.updateGroup(groupName, avatarUrl, groupId);

			String newGroupName = groupDao.getSelfGroupNameById(groupId);

			boolean groupNameChanged = !Objects.equals(oldGroupName, newGroupName);

			List<groupMembersDTO> members = groupMemberDao.getMembersByGroupId(groupId);

			List<Long> receiverIds = members.stream().map(groupMembersDTO::getUser_id)
					.filter(id -> !Objects.equals(id, createdBy)).toList();

			if (receiverIds.isEmpty()) {

				return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
			}

			// 一次查 Email 設定
			List<EmailNotifyUserDTO> userInfos = userInfoDao.findEmailNotifyUsers(receiverIds);

			Map<Long, EmailNotifyUserDTO> userMap = userInfos.stream()
					.collect(Collectors.toMap(EmailNotifyUserDTO::getUserId, Function.identity()));

			String notifyContent = "";

			if (groupNameChanged) {

				notifyContent = selfName + " 已將群組 " + oldGroupName + " 改成 " + newGroupName;

			} else {

				notifyContent = selfName + " 已更改該群組的大頭貼";
			}

			notifacationService.batchInsertNotify(createdBy, receiverIds, notifyContent, "update");

			pushUnreadForUsers(receiverIds);

			notifacationService.sendEmailNotify(receiverIds, notifyContent, userMap);

			return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());

		} catch (Exception e) {

			e.printStackTrace();

			return new BasicResponse("update fail", 500);
		}
	}

	/* 刪除群組前，先將User本人勾選物品轉成私人 */
	@Transactional
	public BasicResponse deleteGroup(Long group_id) {

		groupDao.deleteGroupChatRoom(group_id);

		groupMemberDao.deleteByGroupId(group_id);

		itemsDao.moveGroupItemsToPrivate(group_id);

		groupDao.deleteGroup(group_id);

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
}
