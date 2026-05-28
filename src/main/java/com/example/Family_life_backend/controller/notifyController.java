package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.request.UpdateAllNotifyReq;
import com.example.Family_life_backend.response.BasicResponse;
import com.example.Family_life_backend.service.notifyService;

@RestController
@RequestMapping("/family_life")
@CrossOrigin(origins = "http://localhost:4200")
public class notifyController {

	@Autowired
	private notifyService notifyService;
	
	@PostMapping("read_notify")
	public BasicResponse isRead(@RequestParam("notify_id") Long notify_id) {
		return notifyService.isRead(notify_id);
	}
	
	@PostMapping("read_all_notify")
	public BasicResponse readAllNotify(@RequestBody UpdateAllNotifyReq req) {
		return notifyService.readAllNotify(req);
	}
	
	@PostMapping("delete_notify")
	public BasicResponse deleteNotify(@RequestParam("notify_id") Long notify_id) {
		return notifyService.deleteNotify(notify_id);
	}
	
	@PostMapping("delete_all_isReadNotify")
	public BasicResponse deleteIsReadAllNotify(@RequestBody UpdateAllNotifyReq req) {
		return notifyService.deleteIsReadAllNotify(req);
	}
}
