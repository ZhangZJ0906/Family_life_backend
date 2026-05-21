package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.constants.ReplyMessage;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.entity.PublicInventoryItem;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.request.ChangePwdReq;
import com.example.Family_life_backend.request.UpdateUserAllReq;
import com.example.Family_life_backend.request.UpdateUserInfoReq;
import com.example.Family_life_backend.respond.getUserInfoRes;
import com.example.Family_life_backend.response.BasicRes;

@Service
public class UserService {

	@Autowired
	private UserInfoDao userInfoDao;

	@Autowired
	private groupDao groupDao;

	/* 註冊 */
	public BasicRes addInfo(AddInfoReq req) {

		if (userInfoDao.existsByEmail(req.getEmail())) {
			return new BasicRes(ReplyMessage.EMAIL_EXISTS.getCode(), ReplyMessage.EMAIL_EXISTS.getMessage());
		}

		String now = LocalDateTime.now().toString();
		userInfoDao.insert(req.getEmail(), req.getUserName(), req.getPwd(), req.getAvatar(), req.isNotify(), now);

		return new BasicRes(ReplyMessage.SUCCESS.getCode(), ReplyMessage.SUCCESS.getMessage());
	}

	/* 登入 */
	public BasicRes login(String email, String pwd) {

		UserInfo user = userInfoDao.getByEmail(email);

		if (user == null) {
			return new BasicRes(ReplyMessage.EMAIL_NOT_FOUND.getCode(), ReplyMessage.EMAIL_NOT_FOUND.getMessage());
		}

		if (!user.getPwd().equals(pwd)) {
			return new BasicRes(ReplyMessage.PASSWORD_ERROR.getCode(), ReplyMessage.PASSWORD_ERROR.getMessage());
		}

		return new BasicRes(ReplyMessage.SUCCESS.getCode(), ReplyMessage.SUCCESS.getMessage());
	}

	/* 更改密碼 */
	public BasicRes changePwd(ChangePwdReq req) {
		UserInfo user = userInfoDao.getByEmail(req.getEmail());

		if (user == null) {
			return new BasicRes(ReplyMessage.EMAIL_NOT_FOUND.getCode(), ReplyMessage.EMAIL_NOT_FOUND.getMessage());
		}
		if (!user.getPwd().equals(req.getOldPwd())) {
			return new BasicRes(ReplyMessage.OLD_PASSWORD_ERROR.getCode(),
					ReplyMessage.OLD_PASSWORD_ERROR.getMessage());
		}

		String now = LocalDateTime.now().toString();
		userInfoDao.updatePwd(user.getUserId(), req.getNewPwd(), now);

		return new BasicRes(ReplyMessage.SUCCESS.getCode(), ReplyMessage.SUCCESS.getMessage());
	}

	/* 變更資料 */
	public BasicRes updateInfo(UpdateUserAllReq req) {

		UpdateUserInfoReq user = req.getUserInfo();

		List<PublicInventoryItem> list = req.getPublicInventoryList();

		UserInfo userInfo = userInfoDao.findById(user.getUserId()).orElse(null);

		if (userInfo == null) {
			return new BasicRes(ReplyMessage.USER_NOT_FOUND.getCode(), ReplyMessage.USER_NOT_FOUND.getMessage());
		}

		String userName = user.getUserName() == null ? userInfo.getUserName() : user.getUserName();
		String avatar = user.getAvatar() == null ? user.getAvatar() : user.getAvatar();
		String now = LocalDateTime.now().toString();
		String email = user.getEmail();
		userInfoDao.updateInfo(user.getUserId(), userName, email, avatar, user.isNotifyByEndDate(), user.isNotifyByEmail(), now);

		// 個人清單是否公開
		for (PublicInventoryItem item : list) {
	        groupDao.updatePublicInventoryToThisGroup(
		        item.getPublicInventory(),
	            item.getGroupId(),
	            (long)user.getUserId()
	        );
	    }
		
		return new BasicRes(ReplyMessage.SUCCESS.getCode(), ReplyMessage.SUCCESS.getMessage());
	}
	
	public getUserInfoRes getUserInfo(Long userId) {
		UserInfo userInfo = userInfoDao.getSelfInfoById(userId);
		return new getUserInfoRes(ReplyMessage.SUCCESS.getMessage() ,ReplyMessage.SUCCESS.getCode(),
				(long)userInfo.getUserId(), userInfo.getUserName(), userInfo.getEmail(), userInfo.getAvatar(), userInfo.isNotifyByEmail(),
				userInfo.isNotifyByEndDate());
	}

}
