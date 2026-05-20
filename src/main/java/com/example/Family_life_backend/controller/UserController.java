package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.service.UserService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin (origins = "http://localhost:4200")
@RequestMapping ("/user")
public class UserController {

	@Autowired
    private UserService userService;

	/* 註冊 */
    @PostMapping("/register")
    public BasicRes addUser(@Valid @RequestBody  AddInfoReq req) {
        return userService.addInfo(req);
    }
     
    /* 登入 */
    @GetMapping (value = "/login")
	public BasicRes login(@RequestParam ("email") String email,//
										@RequestParam("password") String pwd) {
		return userService.login(email, pwd);
		
	}
	
	/* 更改密碼 */
	
	/* 變更資料*/
	
    
}
