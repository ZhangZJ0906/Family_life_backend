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

import com.example.Family_life_backend.request.AddSubscriptionReq;
import com.example.Family_life_backend.request.UpdateSubscriptionReq;
import com.example.Family_life_backend.response.SubscriptionRes;
import com.example.Family_life_backend.service.SubscriptionService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    // 查詢某群組的訂閱
    @GetMapping("/getByGroup")
    public SubscriptionRes getByGroup(
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "groupId", required = false) Integer groupId) {

        return subscriptionService.getByGroup(groupId, userId);
    }

    // 新增訂閱
    @PostMapping("/add")
    public SubscriptionRes add(@RequestBody AddSubscriptionReq req) {
        return subscriptionService.add(req);
    }

    // 修改訂閱
    @PostMapping("/update")
    public SubscriptionRes update(@RequestBody UpdateSubscriptionReq req) {
        return subscriptionService.update(req);
    }

    // 刪除訂閱
 // 使用 DELETE 刪除訂閱
    @DeleteMapping("/delete")
    public SubscriptionRes delete(@RequestParam("id") Integer id) {
        return subscriptionService.delete(id);
    }
}
