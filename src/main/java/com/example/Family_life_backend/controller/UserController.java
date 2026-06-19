package com.example.Family_life_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.entity.PublicInventoryItem;
import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.request.ChangePwdReq;
import com.example.Family_life_backend.request.UpdatePasswordReq;
import com.example.Family_life_backend.request.UpdateUserAllReq;
import com.example.Family_life_backend.request.UpdateUserInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.getUserInfoRes;
import com.example.Family_life_backend.service.EmailService;
import com.example.Family_life_backend.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

@RestController
//@CrossOrigin(origins = {
//		  "http://localhost:4200",
//		  "https://zipping-cytoplast-laxative.ngrok-free.dev"
//		})
@CrossOrigin(origins = "http://localhost:4200")
//@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserInfoDao userInfoDao;

	//email驗證碼
    private final Map<String, String> verificationCodes = new HashMap<>();

	@PostMapping("/register")
	public BasicRes addUser(@Valid @RequestBody AddInfoReq req) {
		return userService.addInfo(req);
	}

	@GetMapping(value = "/login")
	public getUserInfoRes login(@RequestParam("email") String email, @RequestParam("password") String pwd) {
		return userService.login(email, pwd);
	}

	@PostMapping("/chang_pwd")
	public BasicRes updatePwd(@Valid @RequestBody ChangePwdReq req) {
		return userService.changePwd(req);
	}


	/* 變更資料 */
	@PostMapping(value = "/update_info", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public BasicRes updateInfo(@RequestPart("userInfo") String userInfoJson,
			@RequestPart("publicInventoryList") String publicInventoryJson,
			@RequestPart(value = "avatar", required = false) MultipartFile avatar) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		UpdateUserInfoReq userInfo = mapper.readValue(userInfoJson, UpdateUserInfoReq.class);
		List<PublicInventoryItem> list = mapper.readValue(publicInventoryJson,
				new TypeReference<List<PublicInventoryItem>>() {
				});

		UpdateUserAllReq req = new UpdateUserAllReq();
		req.setUserInfo(userInfo);
		req.setPublicInventoryList(list);

		return userService.updateInfo(req, avatar);
	}

	@GetMapping("/get_user_info")
	public getUserInfoRes getSelfInfo(@RequestParam("userId") Long userId) {

		return userService.getUserInfo(userId);
	}

	@PostMapping("/send")
	public String sendCode(@RequestParam("email") String email) {
		Random random = new Random();

		String code = String.format("%06d", random.nextInt(1000000));

		verificationCodes.put(email, code);

		emailService.sendVerificationCode(email, code);

		return "驗證碼已寄出";
	}

	@PostMapping("/verify")
	public String verifyCode(@RequestParam("email") String email, @RequestParam("code") String code) {

		String savedCode = verificationCodes.get(email);

		if (savedCode != null && savedCode.equals(code)) {
			verificationCodes.remove(email);
			userInfoDao.updateEmailVerify(email);
			return "驗證成功";
		}

		return "驗證失敗";
	}

//確認Email 2026-05-28 by ZJ
	@GetMapping("/checkEmail")
	public BasicRes checkEmail(@RequestParam("email") String email) {
		return userService.checkEmail(email);
	}

	@PostMapping("/updatePassword")
	public BasicRes updatePassword(@RequestBody UpdatePasswordReq req) {
		System.out.println(req.getEmail() + req.getPassword());
		return userService.updatePassword(req.getEmail(), req.getPassword());
	}

}