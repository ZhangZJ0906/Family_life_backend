package com.example.Family_life_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.constants.ReplyMessage;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.entity.PublicInventoryItem;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.globalVar.globalVar;
import com.example.Family_life_backend.repositary.UserRepository;
import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.request.ChangePwdReq;
import com.example.Family_life_backend.request.UpdateUserAllReq;
import com.example.Family_life_backend.request.UpdateUserInfoReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.getUserInfoRes;

@Service
public class UserService {

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private groupDao groupDao;

	@Autowired
	private globalVar globalVar;

	public BasicRes addInfo(AddInfoReq req) {
		if (userInfoDao.existsByEmail(req.getEmail())) {
			return new BasicRes(ReplyMessage.EMAIL_EXISTS.getMessage(), ReplyMessage.EMAIL_EXISTS.getCode());
		}

		String now = LocalDateTime.now().toString();
		userInfoDao.insert(req.getEmail(), req.getUserName(), req.getPwd(), req.getAvatar(), now);

		UserInfo userInfo = userRepository.findByEmail(req.getEmail());

		LocalDateTime now02 = LocalDateTime.now(ZoneId.of("Asia/Taipei"));

		userInfo.setLoginItemListPageTime(now02);
		userInfo.setLoginCalendarPageTime(now02);
		userInfo.setLoginExpensePageTime(now02);

		userRepository.save(userInfo);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public getUserInfoRes login(String email, String pwd) {
		UserInfo user = userInfoDao.getByEmail(email);

		if (user == null) {
			return new getUserInfoRes(ReplyMessage.EMAIL_NOT_FOUND.getMessage(),
					ReplyMessage.EMAIL_NOT_FOUND.getCode());
		}

		if (!user.getPwd().equals(pwd)) {
			return new getUserInfoRes(ReplyMessage.PASSWORD_ERROR.getMessage(), ReplyMessage.PASSWORD_ERROR.getCode());
		}

		return new getUserInfoRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode(),
				(long) user.getUserId(), user.getUserName(), user.getEmail(), user.getAvatar(),
				user.isNotifyByEndDate(), user.isNotifyByEmail(), user.isEmailVerify());
	}

	public BasicRes changePwd(ChangePwdReq req) {
		UserInfo user = userInfoDao.getByEmail(req.getEmail());

		if (user == null) {
			return new BasicRes(ReplyMessage.USER_NOT_FOUND.getMessage(), ReplyMessage.USER_NOT_FOUND.getCode());
		}
		if (!user.getPwd().equals(req.getOldPwd())) {
			return new BasicRes(ReplyMessage.OLD_PASSWORD_ERROR.getMessage(),
					ReplyMessage.OLD_PASSWORD_ERROR.getCode());
		}

		String now = LocalDateTime.now().toString();
		userInfoDao.updatePwd(user.getUserId(), req.getNewPwd(), now);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public BasicRes updateInfo(UpdateUserAllReq req, MultipartFile avatarFile) {
		UpdateUserInfoReq user = req.getUserInfo();
		List<PublicInventoryItem> list = req.getPublicInventoryList();

		UserInfo userInfo = userInfoDao.findById(user.getUserId()).orElse(null);

		if (userInfo == null) {
			return new BasicRes(ReplyMessage.USER_NOT_FOUND.getMessage(), ReplyMessage.USER_NOT_FOUND.getCode());
		}

		String avatarUrl = userInfo.getAvatar();

		// 只有有新圖片才更新
		if (avatarFile != null && !avatarFile.isEmpty()) {
			try {
				String originalName = avatarFile.getOriginalFilename();
				String ext = ".jpg";

				if (originalName != null && originalName.contains(".")) {
					ext = originalName.substring(originalName.lastIndexOf("."));
				}

				String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID() + ext;

				// Docker volume 對應位置：
				// Windows ./uploads <-> Docker /app/uploads
				Path uploadPath = Paths.get("/app/uploads");

				if (!Files.exists(uploadPath)) {
					Files.createDirectories(uploadPath);
				}

				Path filePath = uploadPath.resolve(fileName);

				Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

				// DB 只存相對路徑，不要存 localhost
				avatarUrl = "/uploads/" + fileName;

			} catch (Exception e) {
				e.printStackTrace();

				return new BasicRes("頭像上傳失敗：" + e.getMessage(), 500);
			}
		}

		String userName = user.getUserName() == null ? userInfo.getUserName() : user.getUserName();

		String email = user.getEmail() == null ? userInfo.getEmail() : user.getEmail();

		String now = LocalDateTime.now(ZoneId.of("Asia/Taipei")).toString();

		userInfoDao.updateInfo(user.getUserId(), userName, email, avatarUrl, user.isNotifyByEndDate(),
				user.isNotifyByEmail(), now);

		if (list != null) {
			for (PublicInventoryItem item : list) {
				groupDao.updatePublicInventoryToThisGroup(item.getPublicInventory(), item.getGroupId(),
						(long) user.getUserId());
			}
		}

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	public getUserInfoRes getUserInfo(Long userId) {
		UserInfo userInfo = userInfoDao.getSelfInfoById(userId);
		if (userInfo == null) {
			return new getUserInfoRes(ReplyMessage.USER_NOT_FOUND.getMessage(), ReplyMessage.USER_NOT_FOUND.getCode());
		}

		return new getUserInfoRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode(),
				(long) userInfo.getUserId(), userInfo.getUserName(), userInfo.getEmail(), userInfo.getAvatar(),
				userInfo.isNotifyByEndDate(), userInfo.isNotifyByEmail(), userInfo.isEmailVerify());
	}

	// 確認Email 2026-05-28 by ZJ
	public BasicRes checkEmail(String email) {

		if (email == null || email.isBlank() || !userInfoDao.existsByEmail(email)) {
			return new BasicRes(ReplyMessage.EMAIL_NOT_FOUND.getMessage(), ReplyMessage.EMAIL_NOT_FOUND.getCode());
		}
		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

	// 更新密碼 2026-05-28 by ZJ
	public BasicRes updatePassword(String email, String password) {
		if (email == null || email.isBlank() || !userInfoDao.existsByEmail(email)) {
			return new BasicRes(ReplyMessage.EMAIL_NOT_FOUND.getMessage(), ReplyMessage.EMAIL_NOT_FOUND.getCode());
		}
		System.out.println(password);
		if (password == null || password.isBlank()) {
			return new BasicRes(ReplyMessage.PASSWORD_ERROR.getMessage(), ReplyMessage.PASSWORD_ERROR.getCode());
		}
		UserInfo user = userInfoDao.getByEmail(email);
		String now = LocalDateTime.now().toString();
		Integer id = user.getUserId();
		userInfoDao.updatePwd(id, password, now);
		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
}