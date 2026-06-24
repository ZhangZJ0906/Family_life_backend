package com.example.Family_life_backend.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.DTO.EmailNotifyUserDTO;
import com.example.Family_life_backend.dao.NotifyDao;

import jakarta.transaction.Transactional;
import com.example.Family_life_backend.entity.notify;

@Service
public class NotificationService {

	@Autowired
	private NotifyDao notifyDao;
	
	@Autowired
	private EmailService emailService;

	@Transactional
	public void batchInsertNotify(Long senderId, List<Long> receiverIds, String content, String type) {

		if (receiverIds == null || receiverIds.isEmpty()) {
			return;
		}

		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Taipei"));

		List<notify> list = receiverIds.stream().map(receiverId -> {

			notify n = new notify();

			n.setSendId(senderId);
			n.setGetUserId(receiverId);
			n.setContent(content);
			n.setType(type);
			n.setRead(false);
			n.setSendDate(now);

			return n;

		}).toList();

		notifyDao.saveAll(list);
	}

	@Async
	public void sendEmailNotify(List<Long> receiverIds, String content, Map<Long, EmailNotifyUserDTO> userMap) {

		receiverIds.forEach(receiverId -> {

			EmailNotifyUserDTO user = userMap.get(receiverId);

			if (user == null) {
				return;
			}

			if (Boolean.TRUE.equals(user.getNotifyByEmail())) {

				try {

					emailService.sendMail(user.getEmail(), "更新通知", content);

				} catch (Exception e) {

					e.printStackTrace();
					throw e; // 🔥 讓 Spring 顯示真正錯誤，不要假裝成 CORS
				}
			}
		});
	}
}