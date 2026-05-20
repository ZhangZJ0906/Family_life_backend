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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.respond.BasicResponse;
import com.example.Family_life_backend.respond.CreateGroupReq;
import com.example.Family_life_backend.respond.GetGroupRes;
import com.example.Family_life_backend.service.groupService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/family_life")
@CrossOrigin(origins = "http://localhost:4200")
public class groupController {
	
	@Autowired
	private groupService groupService;
	
	@PostMapping("create")
	public BasicResponse create(@Valid @RequestBody CreateGroupReq req) {
		return groupService.create(req);
	}
	
	@GetMapping("get_group_list")
	public GetGroupRes getGroup(@RequestParam("user_id") Long user_id) {
		return groupService.getList(user_id);
	}
		
	@PostMapping("update_group")
	public BasicResponse updateGroup(@RequestParam("groupId") Long groupId, @RequestParam("groupName") String groupName, @RequestPart(value = "avatar", required = false) MultipartFile avatar
			, @RequestParam("createdBy") Long createdBy) {
	    return groupService.updateGroup(groupId, groupName, avatar, createdBy);
	}
	
	@DeleteMapping("delete_group/{group_id}")
	public BasicResponse deleteGroup(@PathVariable("group_id") Long group_id) {
	    return groupService.deleteGroup(group_id);
	}
	
}
