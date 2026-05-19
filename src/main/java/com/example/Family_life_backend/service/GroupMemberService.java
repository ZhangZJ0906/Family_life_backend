package com.example.Family_life_backend.service;

import java.io.Console;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.DTO.UserNotifyDTO;
import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.constant.replyMsg;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.GroupMembers;
import com.example.Family_life_backend.req.groupMemberReq;
import com.example.Family_life_backend.req.joinGroupReq;
import com.example.Family_life_backend.respond.BasicResponse;
import com.example.Family_life_backend.respond.GetGroupMemberRes;
import com.example.Family_life_backend.respond.getInvitedMemberRes;
import com.example.Family_life_backend.respond.getNotifyRes;
import com.example.Family_life_backend.entity.notify;

@Service
public class GroupMemberService {
	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private NotifyDao notifyDao;

	@Transactional
	public BasicResponse invite(groupMemberReq req) {
		if (groupMemberDao.checkUserIdExistInGroup(req.getGroup_id(), req.getUser_id()) != 0) {
			return new BasicResponse(replyMsg.USER_ID_EXIST.getMessage(), replyMsg.USER_ID_EXIST.getCode());
		}

		if (groupMemberDao.checkUserIdExist(req.getUser_id()) == 0) {
			return new BasicResponse(replyMsg.USER_ID_NOT_EXIST.getMessage(), replyMsg.USER_ID_NOT_EXIST.getCode());
		}

		if (groupMemberDao.isInvite(req.getUser_id(), req.getGroup_id()) != 0) {
			return new BasicResponse(replyMsg.MEMBER_IS_INVITED.getMessage(), replyMsg.MEMBER_IS_INVITED.getCode());
		}

		req.setUser_name(groupMemberDao.invitedUserName(req.getUser_id()));

		String sendName = groupDao.getSelfName(req.getSendUserId());
		String content = sendName + " 已傳送群組邀請給你";
		String type = "invite";

		groupMemberDao.sendInviteNotify(req.getSendUserId(), req.getUser_id(), content, type, false, req.getGroup_id());
		groupMemberDao.addToInviteMember(req.getUser_id(), req.getGroup_id(), req.getUser_name(), null);
//		groupMemberDao.insert(req.getGroup_id(), req.getUser_id(), 0, req.getUser_name());
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	@Transactional
	public BasicResponse acceptJoinGroup(Long userId, Long groupId, Long notifyId) {
		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);
		String group_name = groupDao.getGroupName(groupId);
		String content = "歡迎" + groupDao.getSelfName(userId) + "加入";

		for (groupMembersDTO member : getGroupMembers) {
			if (member.getUser_id() != userId) {
				notifyDao.sendNewMemberNotify(groupId, member.getUser_id(), content, "group", false, groupId);
			}
		}

		groupMemberDao.insert(groupId, userId, 0, groupDao.getSelfName(userId));
		notifyDao.isReadNotify(notifyId);
		groupMemberDao.deleteInvitedMember(groupId, userId);
		notifyDao.updateInviteNotify("accepted", userId, notifyId);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	@Transactional
	public BasicResponse rejectJoinGroup(Long userId, Long groupId, Long notifyId) {
		notifyDao.isReadNotify(notifyId);
		groupMemberDao.deleteInvitedMember(groupId, userId);
		notifyDao.updateInviteNotify("rejected", userId, notifyId);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	@Transactional
	public BasicResponse join(joinGroupReq req) {
		Long groupId = groupMemberDao.findGroupIdByInviteCode(req.getInviteCode());

		if (groupId == null) {
			return new BasicResponse(replyMsg.GROUP_NOT_EXIST.getMessage(), replyMsg.GROUP_NOT_EXIST.getCode());
		}

		if (groupMemberDao.checkUserIdExistInGroup(groupId, req.getUserId()) != 0) {
			return new BasicResponse(replyMsg.USER_ID_EXIST.getMessage(), replyMsg.USER_ID_EXIST.getCode());
		}

		List<groupMembersDTO> getGroupMembers = groupMemberDao.getMembersByGroupId(groupId);
		String content = "歡迎" + groupDao.getSelfName(req.getUserId()) + "加入";
		String group_name = groupDao.getGroupName(groupId);

		for (groupMembersDTO member : getGroupMembers) {
			if (member.getUser_id() != req.getUserId()) {
				notifyDao.sendNewMemberNotify(groupId, member.getUser_id(), content, "group", false, groupId);
			}
		}

		groupMemberDao.insert(groupId, req.getUserId(), 0, groupDao.getSelfName(req.getUserId()));
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	@Transactional
	public BasicResponse removeMember(Long group_id, Long user_id) {
		groupMemberDao.deleteMember(group_id, user_id);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	public getNotifyRes getNotifyList(Long userId) {
		List<UserNotifyDTO> list = groupMemberDao.getNotifyList(userId);
		return new getNotifyRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(), list);
	}

	public int getNotifyCount(Long getUserId) {
		int count = notifyDao.getNotifyCount(getUserId);
		return count;
	}

	public getInvitedMemberRes getInvitedMemberList(Long group_id) {
		return new getInvitedMemberRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(),
				groupMemberDao.getInvitedMemberList(group_id));
	}

	public GetGroupMemberRes getMemberList(Long groupId) {
		return new GetGroupMemberRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(),
				groupMemberDao.getMembersByGroupId(groupId));
	}
}
