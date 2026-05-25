package com.example.Family_life_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.constants.ReplyMessage;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.request.ChangePwdReq;
import com.example.Family_life_backend.request.UpdateUserInfoReq;
import com.example.Family_life_backend.response.BasicRes;

@Service
public class UserService {

	@Autowired
	private UserInfoDao userInfoDao;

	/* 註冊 */
	public BasicRes addInfo(AddInfoReq req) {

		if (userInfoDao.existsByEmail(req.getEmail())) {
			return new BasicRes(ReplyMessage.EMAIL_EXISTS.getMessage(), ReplyMessage.EMAIL_EXISTS.getCode());
		}

		String now = LocalDateTime.now().toString();
		userInfoDao.insert( req.getUserName(), req.getEmail(),req.getPwd(), req.getAvatar(), now);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
	/* 登入 */
	public BasicRes login(String email, String pwd) {
		
		UserInfo user = userInfoDao.getByEmail(email);

		if (user == null) {
			return new BasicRes(ReplyMessage.EMAIL_NOT_FOUND.getMessage(),
					ReplyMessage.EMAIL_NOT_FOUND.getCode());
		}

		if (!user.getPwd().equals(pwd)) {
			return new BasicRes(ReplyMessage.PASSWORD_ERROR.getMessage(),
					ReplyMessage.PASSWORD_ERROR.getCode());
		}
  
		return new BasicRes(ReplyMessage.SUCCESS.getMessage(),
				ReplyMessage.SUCCESS.getCode());
	}
	
	/* 更改密碼 */
	public BasicRes changePwd(ChangePwdReq req) {
		UserInfo user = userInfoDao.findById(req.getUserId()).orElse(null);

		if(user == null) {
			return new BasicRes(ReplyMessage.USER_NOT_FOUND.getMessage(), ReplyMessage.USER_NOT_FOUND.getCode());
		}
		if(!user.getPwd().equals(req.getOldPwd())) {
			return new BasicRes(ReplyMessage.OLD_PASSWORD_ERROR.getMessage(), ReplyMessage.OLD_PASSWORD_ERROR.getCode());
		}

		String now = LocalDateTime.now().toString();
		userInfoDao.updatePwd(req.getUserId(), req.getNewPwd(), now);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}
	
	/* 變更資料*/
	public BasicRes updateInfo(UpdateUserInfoReq req) {
		UserInfo user = userInfoDao.findById(req.getUserId()).orElse(null);

		if(user == null) {
			return new BasicRes(ReplyMessage.USER_NOT_FOUND.getMessage(), ReplyMessage.USER_NOT_FOUND.getCode());
		}

		String userName = req.getUserName() == null ? user.getUserName() : req.getUserName();
		String avatar = req.getAvatar() == null ? user.getAvatar() : req.getAvatar();
		String now = LocalDateTime.now().toString();
		userInfoDao.updateInfo(req.getUserId(), userName, avatar, req.isNotify(), now);

		return new BasicRes(ReplyMessage.SUCCESS.getMessage(), ReplyMessage.SUCCESS.getCode());
	}

}
