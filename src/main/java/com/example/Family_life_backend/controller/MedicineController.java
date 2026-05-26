package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.request.AddMedicineReq;
import com.example.Family_life_backend.request.UpdateMedicineReq;
import com.example.Family_life_backend.response.MedicineRes;
import com.example.Family_life_backend.service.MedicineService;

@RestController
@RequestMapping("/medicine")
@CrossOrigin(origins = "http://localhost:4200")
public class MedicineController {

    @Autowired
    private MedicineService medicineService;

    @GetMapping("/getByGroup")
    public MedicineRes getByGroup(@RequestParam("groupId") Integer groupId, @RequestParam("userId") Long userId) {
        return medicineService.getByGroup(groupId, userId);
    }

    @PostMapping("/add")
    public MedicineRes add(@RequestBody AddMedicineReq req) {
        return medicineService.add(req);
    }

    @PostMapping("/update")
    public MedicineRes update(@RequestBody UpdateMedicineReq req) {
        return medicineService.update(req);
    }

    @DeleteMapping("/delete")
    public MedicineRes delete(@RequestParam("id") Integer id, @RequestParam("userId") Long userId) {
        return medicineService.delete(id, userId);
    }
}
