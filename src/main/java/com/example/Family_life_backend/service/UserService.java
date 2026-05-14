package com.example.Family_life_backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.constants.ReplyMessage;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.entity.UserInfo;
import com.example.Family_life_backend.request.AddInfoReq;
import com.example.Family_life_backend.response.BasicRes;

@Service
public class UserService {

	@Autowired
	private UserInfoDao userInfoDao;

	/* 註冊 */
	public BasicRes addInfo(AddInfoReq req) {

		// 1. 檢查Email重複
        if (userInfoDao.existsByEmail(req.getEmail())) {
            return new BasicRes(ReplyMessage.EMAIL_EXISTS.getCode(), ReplyMessage.EMAIL_EXISTS.getMessage());
        }

        // 2. 自動生成時間
        String now = LocalDateTime.now().toString(); 

        // 3. 呼叫 Dao 存檔 (建議 Dao 也可以改為接收 Entity 或 Req 對象)
        userInfoDao.insert( req.getEmail(), req.getUserName(), req.getPwd(), req.getAvatar(), req.isNotify(), now);

        return new BasicRes(ReplyMessage.SUCCESS.getCode(), ReplyMessage.SUCCESS.getMessage());
    }
	
	/* 登入 */
	public BasicRes login(String email, String pwd) {
		
		UserInfo user = userInfoDao.getByEmail(email);

		if (user == null) {
			return new BasicRes(ReplyMessage.EMAIL_NOT_FOUND.getCode(),
					ReplyMessage.EMAIL_NOT_FOUND.getMessage());
		}

		if (!user.getPwd().equals(pwd)) {
			return new BasicRes(ReplyMessage.PASSWORD_ERROR.getCode(),
					ReplyMessage.PASSWORD_ERROR.getMessage());
		}
  
		return new BasicRes(ReplyMessage.SUCCESS.getCode(),
				ReplyMessage.SUCCESS.getMessage());
	}
	
	/* 更改密碼 */
	
	/* 變更資料*/

}
