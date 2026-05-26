package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.example.Family_life_backend.request.CalendarReq;
import com.example.Family_life_backend.response.CalendarRes;
import com.example.Family_life_backend.service.CalendarService;

@RestController
@RequestMapping("/calendar")
@CrossOrigin(origins = "http://localhost:4200")
public class CalendarController {

	@Autowired
	private CalendarService calendarService;

	// 新增事件
	@PostMapping("/create")
	public CalendarRes create(@RequestBody CalendarReq req) {
		return calendarService.create(req);
	}

	// 查詢某一個家庭群組的所有行事曆事件
	@GetMapping("/group/{groupId}")
	public CalendarRes getByGroup(@PathVariable("groupId") Long groupId) {
		return calendarService.getByGroup(groupId);
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

	// 更新事件
	@PutMapping("/{id}")
	public CalendarRes update(@PathVariable("id") Long id, @RequestBody CalendarReq req) {
		return calendarService.update(id, req);
	}

	// 刪除事件
	@DeleteMapping("/{id}")
	public CalendarRes delete(@PathVariable("id") Long id) {
		return calendarService.delete(id);
	}
}