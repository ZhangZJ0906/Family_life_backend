package com.example.Family_life_backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.constant.replyMsg;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.GroupMembers;
import com.example.Family_life_backend.entity.group;
import com.example.Family_life_backend.repositary.GroupRepository;
import com.example.Family_life_backend.respond.BasicResponse;
import com.example.Family_life_backend.respond.CreateGroupReq;
import com.example.Family_life_backend.respond.GetGroupRes;
import com.example.Family_life_backend.vo.GroupMembersVo;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class groupService {

	@Autowired
	private groupDao groupDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private GroupRepository groupRepository;

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
		return new GetGroupRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(), groupDao.getAll(user_id));
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
				}
			}

			return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());

		} catch (Exception e) {

			e.printStackTrace();

			return new BasicResponse("update fail", 500);

		}

	}

	@Transactional
	public BasicResponse deleteGroup(Long group_id) {
		groupMemberDao.deleteByGroupId(group_id);
		groupDao.deleteGroup(group_id);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
}
