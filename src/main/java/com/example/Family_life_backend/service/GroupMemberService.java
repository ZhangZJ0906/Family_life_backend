package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.DTO.EmailNotifyUserDTO;
import com.example.Family_life_backend.DTO.UserNotifyDTO;
import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.constant.replyMsg;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;

import com.example.Family_life_backend.request.groupMemberReq;
import com.example.Family_life_backend.request.joinGroupReq;
import com.example.Family_life_backend.response.getInviteMembersRes;
import com.example.Family_life_backend.response.BasicResponse;
import com.example.Family_life_backend.response.GetGroupIdByUserIdRes;
import com.example.Family_life_backend.response.GetGroupMemberRes;
import com.example.Family_life_backend.response.getNotifyRes;

@Service
public class GroupMemberService {

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private groupMemberDao groupMemberDao;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	@Autowired
	private NotificationService notifacationService;

	// =========================
	// Invite
	// =========================
	@Transactional
	public BasicResponse invite(groupMemberReq req) {

		if (groupMemberDao.checkUserExistInGroupByEmail(req.getGroup_id(), req.getEmail()) != 0) {
			return new BasicResponse(replyMsg.USER_ID_EXIST.getMessage(), replyMsg.USER_ID_EXIST.getCode());
		}

		if (groupMemberDao.checkUserEmailExist(req.getEmail()) == 0) {
			return new BasicResponse(replyMsg.USER_ID_NOT_EXIST.getMessage(), replyMsg.USER_ID_NOT_EXIST.getCode());
		}

		if (groupMemberDao.isInvite(req.getEmail(), req.getGroup_id()) != 0) {
			return new BasicResponse(replyMsg.MEMBER_IS_INVITED.getMessage(), replyMsg.MEMBER_IS_INVITED.getCode());
		}

		Long userId = userInfoDao.getUIDByEmail(req.getEmail());
		req.setUser_id(userId);
		req.setUser_name(groupMemberDao.invitedUserName(userId));

		String sendName = groupDao.getSelfName(req.getSendUserId());
		String content = sendName + " 已傳送群組邀請給你";

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"));

		groupMemberDao.sendInviteNotify(req.getSendUserId(), userId, content, "invite", false, req.getGroup_id(), now);

		groupMemberDao.addToInviteMember(userId, req.getGroup_id());

		sendEmailIfEnabled(userId, "邀請通知", content);

		pushUnread(userId);

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	// =========================
	// Accept Join Group
	// =========================
	@Transactional
	public BasicResponse acceptJoinGroup(Long userId, Long groupId, Long notifyId) {

		groupMemberDao.insert(groupId, userId, 0);

		notifyDao.isReadOneNotify(notifyId);
		groupMemberDao.deleteInvitedMember(groupId, userId);
		notifyDao.updateInviteNotify("accepted", userId, notifyId);

		notifyGroupMembers(groupId, userId);

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	// =========================
	// Reject Join Group
	// =========================
	@Transactional
	public BasicResponse rejectJoinGroup(Long userId, Long groupId, Long notifyId) {

		notifyDao.isReadOneNotify(notifyId);
		groupMemberDao.deleteInvitedMember(groupId, userId);
		notifyDao.updateInviteNotify("rejected", userId, notifyId);

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	// =========================
	// Join by invite code
	// =========================
	@Transactional
	public BasicResponse join(joinGroupReq req) {

		Long groupId = groupMemberDao.findGroupIdByInviteCode(req.getInviteCode());

		if (groupId == null) {
			return new BasicResponse(replyMsg.GROUP_NOT_EXIST.getMessage(), replyMsg.GROUP_NOT_EXIST.getCode());
		}

		if (groupMemberDao.checkUserIdExistInGroup(groupId, req.getUserId()) != 0) {
			return new BasicResponse(replyMsg.USER_ID_EXIST.getMessage(), replyMsg.USER_ID_EXIST.getCode());
		}

		groupMemberDao.insert(groupId, req.getUserId(), 0);

		groupMemberDao.deleteInvitedMember(groupId, req.getUserId());
		groupMemberDao.deleteInvitedMemberNotify(groupId, req.getUserId());

		notifyGroupMembers(groupId, req.getUserId());

		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	// =========================
	// Remove member
	// =========================
	@Transactional
	public BasicResponse removeMember(Long groupId, Long userId) {
		groupMemberDao.deleteMember(groupId, userId);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}

	// =========================
	// Get notify list
	// =========================
	public getNotifyRes getNotifyList(Long userId) {
		List<UserNotifyDTO> list = groupMemberDao.getNotifyList(userId);
		return new getNotifyRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(), list);
	}

	public int getNotifyCount(Long userId) {
		return notifyDao.getNotifyCount(userId);
	}

	public getInviteMembersRes getInvitedMemberList(Long groupId) {
		return new getInviteMembersRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(),
				groupMemberDao.getInvitedMemberList(groupId));
	}

	public GetGroupMemberRes getMemberList(Long groupId) {
		return new GetGroupMemberRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(),
				groupMemberDao.getMembersByGroupId(groupId));
	}

	// =========================
	// Get group ids by user
	// =========================
	public GetGroupIdByUserIdRes getGroupIdList(Long userId) {

		List<Object[]> list = groupMemberDao.getGroupIdByUserId(userId);

		Map<Long, String> idMap = list.stream().filter(row -> row[0] != null)
				.collect(Collectors.toMap(row -> ((Number) row[0]).longValue(),
						row -> row[1] != null ? row[1].toString() : "未命名群組", (a, b) -> a));

		return new GetGroupIdByUserIdRes(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode(), idMap);
	}

	// =====================================================
	// 🔥 核心：通知群組成員（重構重點）
	// =====================================================
	private void notifyGroupMembers(Long groupId, Long joinedUserId) {

		String joinUserName = groupDao.getSelfName(joinedUserId);

		String content = "歡迎" + joinUserName + "加入";

		List<groupMembersDTO> members = groupMemberDao.getMembersByGroupId(groupId);

		List<Long> receiverIds = members.stream().map(groupMembersDTO::getUser_id)
				.filter(id -> !Objects.equals(id, joinedUserId)).toList();

		// 一次查 Email 設定
		List<EmailNotifyUserDTO> userInfos = userInfoDao.findEmailNotifyUsers(receiverIds);

		Map<Long, EmailNotifyUserDTO> userMap = userInfos.stream()
				.collect(Collectors.toMap(EmailNotifyUserDTO::getUserId, Function.identity()));

		// 寫通知
		notifacationService.batchInsertNotify(groupId, receiverIds, content, "group");

		// 一次查所有未讀數
		List<Object[]> result = notifyDao.countUnreadByUserIds(receiverIds);

		Map<Long, Integer> unreadMap = result.stream()
				.collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).intValue()));

		// 推播
		for (Long memberId : receiverIds) {

			notifySocketService.pushUnreadCount(memberId, unreadMap.getOrDefault(memberId, 0));
		}

		notifacationService.sendEmailNotify(receiverIds, content, userMap);
	}

	// =====================================================
	// Email
	// =====================================================
	private void sendEmailIfEnabled(Long userId, String title, String content) {
		if (userInfoDao.getEmailNotifyById(userId)) {
			emailService.sendMail(userInfoDao.getEmailById(userId), title, content);
		}
	}

	// =====================================================
	// Socket unread
	// =====================================================
	private void pushUnread(Long userId) {
		int unreadCount = notifyDao.countUnreadByUserId(userId);
		notifySocketService.pushUnreadCount(userId, unreadCount);
	}

}