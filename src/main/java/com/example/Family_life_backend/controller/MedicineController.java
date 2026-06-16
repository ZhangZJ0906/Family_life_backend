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

import com.example.Family_life_backend.request.AddMedicineReq;
import com.example.Family_life_backend.request.UpdateMedicineReq;
import com.example.Family_life_backend.request.UpdateNotifyReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.MedicineRes;
import com.example.Family_life_backend.service.MedicineService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/medicine")
//@CrossOrigin(origins = "http://localhost:4200")
@CrossOrigin(origins = "*")
public class MedicineController {

	@Autowired
	private MedicineService medicineService;

	@GetMapping("/getByGroup")
	public MedicineRes getByGroup(@RequestParam("userId") Integer userId,
			@RequestParam(value = "groupId", required = false) Integer groupId) {

		return medicineService.getByGroup(groupId, userId);
	}

	@PostMapping("/add")
	public MedicineRes add(@RequestPart("req") AddMedicineReq req,
			@RequestPart(value = "avatar", required = false) MultipartFile image) {
		return medicineService.add(req, image);
	}

	@PostMapping("/update")
	public MedicineRes update(@RequestPart("req") UpdateMedicineReq req,
			@RequestPart(value = "avatar", required = false) MultipartFile image) {
		return medicineService.update(req, image);
	}

	@PostMapping("/updateNotify")
	public BasicRes updateNotify(@Valid @RequestBody UpdateNotifyReq req) {
		return medicineService.updateNotify(req);
	}

	@DeleteMapping("/delete")
	public MedicineRes delete(@RequestParam("id") Integer id, @RequestParam("userId") Long userId) {
		return medicineService.delete(id, userId);
	}
}
