package com.example.Family_life_backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.CalendarDao;
import com.example.Family_life_backend.entity.Calendar;
import com.example.Family_life_backend.request.CalendarReq;
import com.example.Family_life_backend.response.CalendarRes;

@Service
public class CalendarService {

	@Autowired
	private CalendarDao calendarDao;
	
	//新增事件
	public CalendarRes create(CalendarReq req) {

	    if (req.getGroupId() == null) {
	        return new CalendarRes(400, "groupId 不可為空");
	    }

	    if (req.getCreatedBy() == null) {
	        return new CalendarRes(400, "createdBy 不可為空");
	    }

	    if (req.getTitle() == null || req.getTitle().isBlank()) {
	        return new CalendarRes(400, "活動名稱不可為空");
	    }

	    if (req.getEventTime() == null) {
	        return new CalendarRes(400, "活動時間不可為空");
	    }
	    
	    if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
	        return new CalendarRes(400, "開始時間不可大於結束時間");
	    }

	    int result = calendarDao.insertCalendarEvent(
	            req.getGroupId(),
	            req.getCreatedBy(),
	            req.getTitle(),
	            req.getDescription(),
	            req.getEventTime(),
	            req.getEndTime(),
	            req.getNotifyBefore()
	    );

	    if (result > 0) {
	        return new CalendarRes(200, "新增成功");
	    }

	    return new CalendarRes(500, "新增失敗");
	}
	
	//修改事件
	public CalendarRes update(Long id, CalendarReq req) {

		if (req.getEventTime() == null) {
		    return new CalendarRes(400, "活動開始時間不可為空");
		}

		if (req.getEndTime() != null && req.getEventTime().isAfter(req.getEndTime())) {
		    return new CalendarRes(400, "開始時間不可大於結束時間");
		}
		
	    int result = calendarDao.updateCalendarEvent(
	            id,
	            req.getTitle(),
	            req.getDescription(),
	            req.getEventTime(),
	            req.getEndTime(),
	            req.getNotifyBefore()
	    );

	    if (result > 0) {
	        return new CalendarRes(200, "修改成功");
	    }

	    return new CalendarRes(404, "查無此事件");
	}
	
	//刪除事件
	public CalendarRes delete(Long id) {

	    int result = calendarDao.deleteCalendarEvent(id);

	    if (result > 0) {
	        return new CalendarRes(200, "刪除成功");
	    }

	    return new CalendarRes(404, "查無此事件");
	}
	
	//查詢某一個家庭群組的所有行事曆事件
	public CalendarRes getByGroup(Long groupId) {
	    List <Calendar> list = calendarDao.findByGroupIdOrderByEventTimeAsc(groupId);
	    return new CalendarRes(200, "查詢成功", list);
	}
	
	public CalendarRes getById(Long id) {
	    Optional<Calendar> op = calendarDao.findById(id);

	    if (op.isEmpty()) {
	        return new CalendarRes(404, "查無此事件");
	    }

	    return new CalendarRes(200, "查詢成功", op.get());
	}
}
