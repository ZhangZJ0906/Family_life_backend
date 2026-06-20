package com.example.Family_life_backend.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.dao.CalendarDao;
import com.example.Family_life_backend.request.CalendarReq;
import com.example.Family_life_backend.response.CalendarRes;
import com.example.Family_life_backend.service.CalendarService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/calendar")
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://localhost:8080")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;
	
	@Autowired
	private CalendarDao calendarDao;
	
	@GetMapping("/getLoginCalendarPageTime")
	public LocalDateTime getLoginItemPageTime(@RequestParam("userId") Integer userId) {
		return calendarDao.getLoginCalendarPageTime((long) userId);
	}

	// 新增事件
	@PostMapping("/create")
	public CalendarRes create(@Valid @RequestBody CalendarReq req) {
		return calendarService.create(req);
	}

	// 查詢某一個家庭群組的所有行事曆事件
	@GetMapping("/getByGroup")
	public CalendarRes getByGroup(@RequestParam("groupId") Long groupId,
	                              @RequestParam("userId") Long userId) {
	    return calendarService.getByGroup(groupId, userId);
	}

	@PostMapping("/recordLoginCalendarPageTime")
	public void recordLoginCalendarPageTime(@RequestParam("userId") Long userId) {
	    calendarDao.recordLoginCalendarPageTime(userId, LocalDateTime.now());
	}

	// 2026-05- 24 by ZJ 新get 資訊
	@GetMapping("/getUserEventInfo")
	public CalendarRes getCalendarEvents(@RequestParam(value = "groupId", required = false) Long groupId,
			@RequestParam(value = "userId", required = false) Long userId) {
		return calendarService.getCalendarEvents(groupId, userId);
	}

	@GetMapping("/{id}")
	public CalendarRes getById(@PathVariable("id") Long id) {
		return calendarService.getById(id);
	}

	// 查詢群組中「指派給目前登入者」的行事曆
	@GetMapping("/group/{groupId}")
	public CalendarRes getGroupCalendarByAssignedUser(@PathVariable("groupId") Long groupId,
			@RequestParam("userId") Long userId) {

		return calendarService.getGroupCalendarByAssignedUser(groupId, userId);
	}

	// 更新事件
	@PutMapping("/{id}")
	public CalendarRes update(@PathVariable("id") Long id, @RequestBody CalendarReq req) {
		return calendarService.update(id, req);
	}

	// 刪除事件
	@DeleteMapping("/{id}/{userId}/{groupId}")
	public CalendarRes delete(@PathVariable("id") Long id, @PathVariable("userId") Long userId,
			@PathVariable("groupId") Long groupId) {
		return calendarService.delete(id, userId, groupId);
	}

	// 查詢同一批活動目前指派的成員 ID
	@GetMapping("/batchAssignedUsers")
	public CalendarRes getBatchAssignedUsers(@RequestParam("eventBatchId") String eventBatchId) {

		return calendarService.getBatchAssignedUsers(eventBatchId);
	}

}