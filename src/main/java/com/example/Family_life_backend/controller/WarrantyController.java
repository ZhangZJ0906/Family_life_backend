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

import com.example.Family_life_backend.request.AddWarrantyReq;
import com.example.Family_life_backend.request.UpdateWarrantyReq;
import com.example.Family_life_backend.response.WarrantyRes;
import com.example.Family_life_backend.service.WarrantyService;

@RestController
@RequestMapping("/warranty")
@CrossOrigin(origins = "http://localhost:4200")
public class WarrantyController {

    @Autowired
    private WarrantyService warrantyService;

    @GetMapping("/getByGroup")
    public WarrantyRes getByGroup(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "groupId", required = false) Integer groupId) {

        return warrantyService.getByGroup(groupId, userId);
    }
    @PostMapping("/add")
    public WarrantyRes add(@RequestBody AddWarrantyReq req) {
        return warrantyService.add(req);
    }

    @PostMapping("/update")
    public WarrantyRes update(@RequestBody UpdateWarrantyReq req) {
        return warrantyService.update(req);
    }

    @DeleteMapping("/delete")
    public WarrantyRes delete(@RequestParam("id") Integer id) {
        return warrantyService.delete(id);
    }
}
