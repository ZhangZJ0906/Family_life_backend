package com.example.Family_life_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Family_life_backend.constant.replyMsg;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.request.UpdateAllNotifyReq;
import com.example.Family_life_backend.response.BasicResponse;

@Service
public class notifyService {
	@Autowired
	NotifyDao notifyDao;
	
	@Transactional
	public BasicResponse isRead(Long notify_id) {
		notifyDao.isReadNotify(notify_id);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
	
	@Transactional
	public BasicResponse readAllNotify(UpdateAllNotifyReq req) {
		List<Long> noyifyIdList = req.getIds();
		for(Long notifyId: noyifyIdList) {
			notifyDao.isReadNotify(notifyId);
		}
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
	
	@Transactional
	public BasicResponse deleteNotify(Long notify_id) {
		notifyDao.deleteNotify(notify_id);
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
	
	@Transactional
	public BasicResponse deleteIsReadAllNotify(UpdateAllNotifyReq req) {
		List<Long> noyifyIdList = req.getIds();
		for(Long notifyId: noyifyIdList) {
			notifyDao.deleteNotify(notifyId);
		}
		return new BasicResponse(replyMsg.SUCCESS.getMessage(), replyMsg.SUCCESS.getCode());
	}
}
