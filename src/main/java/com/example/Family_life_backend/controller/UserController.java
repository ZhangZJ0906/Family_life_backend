package com.example.Family_life_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.entity.PublicInventoryItem;
import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.request.ChangePwdReq;
import com.example.Family_life_backend.request.UpdateUserAllReq;
import com.example.Family_life_backend.request.UpdateUserInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.getUserInfoRes;
import com.example.Family_life_backend.service.UserService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    /* 註冊 */
    @PostMapping("/register")
    public BasicRes addUser(@Valid @RequestBody AddInfoReq req) {
        return userService.addInfo(req);
    }

    /* 登入 */
    @GetMapping("/login")
    public getUserInfoRes login(
            @RequestParam("email") String email,
            @RequestParam("password") String pwd) {

        return userService.login(email, pwd);
    }

    /* 更改密碼 */
    @PostMapping("/chang_pwd")
    public BasicRes updatePwd(@Valid @RequestBody ChangePwdReq req) {
        return userService.changePwd(req);
    }

    /* 變更資料 */
    @PostMapping(
            value = "/update_info",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public BasicRes updateInfo(
            @RequestPart("userInfo") String userInfoJson,
            @RequestPart("publicInventoryList") String publicInventoryJson,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        // JSON -> DTO
        UpdateUserInfoReq userInfo =
                mapper.readValue(userInfoJson, UpdateUserInfoReq.class);

        // JSON -> List
        List<PublicInventoryItem> list =
                mapper.readValue(
                        publicInventoryJson,
                        new TypeReference<List<PublicInventoryItem>>() {}
                );

        // 組 req
        UpdateUserAllReq req = new UpdateUserAllReq();

        req.setUserInfo(userInfo);
        req.setPublicInventoryList(list);

        System.out.println(userInfoJson);
        System.out.println(avatar);

        return userService.updateInfo(req, avatar);
    }

    @GetMapping("/get_user_info")
    public getUserInfoRes getSelfInfo(
            @RequestParam("userId") Long userId) {

        return userService.getUserInfo(userId);
    }
}
