package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
import com.example.Family_life_backend.req.groupMemberReq;
import com.example.Family_life_backend.req.joinGroupReq;
import com.example.Family_life_backend.respond.BasicResponse;
import com.example.Family_life_backend.respond.GetGroupMemberRes;
import com.example.Family_life_backend.respond.getInviteMembersRes;
import com.example.Family_life_backend.respond.getNotifyRes;
=======
import com.example.Family_life_backend.request.groupMemberReq;
import com.example.Family_life_backend.request.joinGroupReq;
import com.example.Family_life_backend.response.BasicResponse;
import com.example.Family_life_backend.response.GetGroupIdByUserIdRes;
import com.example.Family_life_backend.response.GetGroupMemberRes;
import com.example.Family_life_backend.response.getInvitedMemberRes;
import com.example.Family_life_backend.response.getNotifyRes;
>>>>>>> origin/ZJ
import com.example.Family_life_backend.service.GroupMemberService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/family_life")
@CrossOrigin(origins = "http://localhost:4200")
public class groupMemberController {

	@Autowired
	private GroupMemberService groupMemberService;
	
	@PostMapping("invite")
	public BasicResponse invite(@Valid @RequestBody groupMemberReq req)  {
		return groupMemberService.invite(req);
	}
	
	@PostMapping("join")
	public BasicResponse join(@Valid @RequestBody joinGroupReq req) {
		return groupMemberService.join(req);
	}
	
	@GetMapping("get_notify")
	public getNotifyRes getNotifyList(@RequestParam("user_id") Long user_id) {
		return groupMemberService.getNotifyList(user_id);
	}
	
	@GetMapping("get_notify_count")
	public int getNotifyCount(@RequestParam("user_id") Long user_id) {
		return groupMemberService.getNotifyCount(user_id);
	}
	
	@PostMapping("accept_join_group")
	public BasicResponse acceptJoinGroup(@RequestParam("user_id") Long user_id, @RequestParam("group_id") Long group_id, 
			@RequestParam("notify_id") Long notify_id) {
		return groupMemberService.acceptJoinGroup(user_id, group_id, notify_id);
	}
	
	@PostMapping("reject_join_group")
	public BasicResponse rejectJoinGroup(@RequestParam("user_id") Long user_id, @RequestParam("group_id") Long group_id, 
			@RequestParam("notify_id") Long notify_id) {
		return groupMemberService.rejectJoinGroup(user_id, group_id, notify_id);
	}
	
	@GetMapping("get_members")
	public GetGroupMemberRes getGroupMembers(@RequestParam("group_id") Long group_id) {
		return groupMemberService.getMemberList(group_id);
	}
	
	@GetMapping("get_invited_members")
	public getInviteMembersRes getInvitedMembers(@RequestParam("group_id") Long group_id) {
		return groupMemberService.getInvitedMemberList(group_id);
	}
	
	@DeleteMapping("delete_group_member/{group_id}/{user_id}")
	public BasicResponse removeMember(
	        @PathVariable("group_id") Long group_id,
	        @PathVariable("user_id") Long user_id
	) {
	    return groupMemberService.removeMember(group_id, user_id);
	}

	/* 透過 user Id 去尋找 他加入的群組 202605-21 by zj */
	@GetMapping("getGroupList")
	public GetGroupIdByUserIdRes getGroupIdList(@RequestParam("user_Id") Long userId) {
		return groupMemberService.getGroupIdList(userId);
	}
}
