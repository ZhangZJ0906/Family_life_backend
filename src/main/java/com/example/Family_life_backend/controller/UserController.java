package com.example.Family_life_backend.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Duration;
import java.time.LocalDateTime;

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
	private static final int VERIFICATION_EXPIRE_MINUTES = 5;
	private static final int RESEND_COOLDOWN_SECONDS = 60;
	
    private final Map<String, VerificationCodeInfo> verificationCodes = new ConcurrentHashMap<>();

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

	// 寄送驗證
	@PostMapping("/send")
	public String sendCode(@RequestParam("email") String email) {
	    LocalDateTime now = LocalDateTime.now();
	    VerificationCodeInfo oldInfo = verificationCodes.get(email);

	    if (oldInfo != null && oldInfo.getLastSentAt().plusSeconds(RESEND_COOLDOWN_SECONDS).isAfter(now)) {
	        long waitSeconds = Duration.between(
	            now,
	            oldInfo.getLastSentAt().plusSeconds(RESEND_COOLDOWN_SECONDS)
	        ).getSeconds();

	        return "請 " + waitSeconds + " 秒後再重新發送驗證碼";
	    }

	    Random random = new Random();
	    String code = String.format("%06d", random.nextInt(1000000));

	    verificationCodes.put(
	        email,
	        new VerificationCodeInfo(
	            code,
	            now.plusMinutes(VERIFICATION_EXPIRE_MINUTES),
	            now
	        )
	    );

	    emailService.sendVerificationCode(email, code);

	    return "驗證碼已寄出";
	}

	@PostMapping("/verify")
	public String verifyCode(@RequestParam("email") String email, @RequestParam("code") String code) {
	    VerificationCodeInfo savedInfo = verificationCodes.get(email);

	    if (savedInfo == null) {
	        return "請先發送驗證碼";
	    }

	    if (savedInfo.getExpiresAt().isBefore(LocalDateTime.now())) {
	        verificationCodes.remove(email);
	        return "驗證碼已過期，請重新發送";
	    }

	    if (savedInfo.getCode().equals(code)) {
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
	
	// 只存在記憶體
	private static class VerificationCodeInfo {
	    private final String code;
	    private final LocalDateTime expiresAt;
	    private final LocalDateTime lastSentAt;

	    private VerificationCodeInfo(String code, LocalDateTime expiresAt, LocalDateTime lastSentAt) {
	        this.code = code;
	        this.expiresAt = expiresAt;
	        this.lastSentAt = lastSentAt;
	    }

	    private String getCode() {
	        return code;
	    }

	    private LocalDateTime getExpiresAt() {
	        return expiresAt;
	    }

	    private LocalDateTime getLastSentAt() {
	        return lastSentAt;
	    }
	}

}