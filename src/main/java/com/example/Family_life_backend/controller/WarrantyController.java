package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.request.AddWarrantyReq;
import com.example.Family_life_backend.request.UpdateNotifyReq;
import com.example.Family_life_backend.request.UpdateWarrantyReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.WarrantyRes;
import com.example.Family_life_backend.service.WarrantyService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/warranty")
@CrossOrigin(origins = "http://localhost:4200")
public class WarrantyController {

	@Autowired
	private WarrantyService warrantyService;

	@GetMapping("/getByGroup")
	public WarrantyRes getByGroup(@RequestParam("userId") Integer userId,
			@RequestParam(value = "groupId", required = false) Integer groupId) {

		return warrantyService.getByGroup(groupId, userId);
	}

	@PostMapping("/add")
	public WarrantyRes add(@RequestPart("req") AddWarrantyReq req,
			@RequestPart(value = "avatar", required = false) MultipartFile image) {
		return warrantyService.add(req, image);
	}

	@PostMapping("/update")
	public WarrantyRes update(@RequestPart("req") UpdateWarrantyReq req,
			@RequestPart(value = "avatar", required = false) MultipartFile image) {
		return warrantyService.update(req, image);
	}

	@PostMapping("/updateNotify")
	public BasicRes updateNotify(@Valid @RequestBody UpdateNotifyReq req) {
		return warrantyService.updateNotify(req);
	}

	@DeleteMapping("/delete")
	public WarrantyRes delete(@RequestParam("id") Integer id, @RequestParam("userId") Long userId) {
		return warrantyService.delete(id, userId);
	}
}
