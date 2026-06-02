package com.example.Family_life_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.constant.replyMsg;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.group;
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

		group saved = groupRepository.save(group);

		groupMemberDao.insert(saved.getGroupId(), saved.getCreatedBy(), 0);

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	public GetGroupRes getList(Long user_id) {
		System.out.print("groupList: " + groupDao.getMyGroupsPublicInventory(user_id));
		return new GetGroupRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(), groupDao.getMyGroups(user_id),
				groupDao.getMyGroupsPublicInventory(user_id));
	}

	@Transactional
	public BasicResponse updateGroup(Long groupId, String groupName, MultipartFile avatar, Long createdBy) {

		String avatarUrl = null;

		String selfName = groupDao.getSelfName(createdBy);

		String oldGroupName = groupDao.getSelfGroupNameById(groupId);

		try {

			// 先拿舊的 avatar（重要）
			String oldAvatar = groupDao.getAvatarByGroupId(groupId);

			avatarUrl = oldAvatar;

			// 只有有新圖片才更新
			if (avatar != null && !avatar.isEmpty()) {

				String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();

				Path uploadPath = Paths.get("uploads");

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);

				Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				avatarUrl = "http://localhost:8080/uploads/" + fileName;
			}

			groupDao.updateGroup(groupName, avatarUrl, createdBy, selfName, groupId);

			String NewGroupId = groupDao.getSelfGroupNameById(groupId);

			String content = selfName + "已將群組" + oldGroupName + "改成" + NewGroupId;

			List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);

			for (groupMembersDTO member : getGroupMembers) {
				if (member.getUser_id() != createdBy) {
					notifyDao.sendGroupNameUpdateNotify(groupId, member.getUser_id(), content, "update", false);

					// 🔥 正確：要重新查 unread count
					int unreadCount = notifyDao.countUnreadByUserId(member.getUser_id());

					if (userInfoDao.getEmailNotifyById(member.getUser_id()) == true) {
						emailService.sendMail(userInfoDao.getEmailById(member.getUser_id()), "更新通知", content);
					}

					notifySocketService.pushUnreadCount(member.getUser_id(), unreadCount);
				}
			}

			return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());

		} catch (Exception e) {

			e.printStackTrace();

			return new BasicResponse("update fail", 500);

		}

	}

	/* 刪除群組前，先將User本人勾選物品轉成私人*/
	@Transactional
	public BasicResponse deleteGroup(Long group_id) {
	    itemsDao.moveGroupItemsToPrivate(group_id);

	    groupMemberDao.deleteByGroupId(group_id);
	    groupDao.deleteGroup(group_id);

	    return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
}
