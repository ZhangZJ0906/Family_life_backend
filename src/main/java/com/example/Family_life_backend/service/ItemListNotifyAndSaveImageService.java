package com.example.Family_life_backend.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.Family_life_backend.DTO.groupMembersDTO;
import com.example.Family_life_backend.dao.ItemsDao;
import com.example.Family_life_backend.dao.NotifyDao;
import com.example.Family_life_backend.dao.UserInfoDao;
import com.example.Family_life_backend.dao.groupDao;
import com.example.Family_life_backend.dao.groupMemberDao;
import com.example.Family_life_backend.entity.UserInfo;

@Service
public class ItemListNotifyAndSaveImageService {

	@Autowired
	private groupMemberDao groupMemberDao;
	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ItemsDao itemDao;
	@Autowired
	private NotifyDao notifyDao;

	@Autowired
	private NotifySocketService notifySocketService;

	@Async
	public void notifyGroupMembers(int groupId, long operatorUserId, String content) {

		List<groupMembersDTO> members = groupMemberDao.getMembersByGroupId((long) groupId);

		// 一次撈所有 member 的 userInfo（排除操作者本人）
		List<Long> memberIds = members.stream().map(groupMembersDTO::getUser_id).filter(id -> id != operatorUserId)
				.collect(Collectors.toList());

		if (memberIds.isEmpty())
			return;

		// 批次查詢，N個人只打一次DB
		Map<Long, UserInfo> userInfoMap = userInfoDao.getSelfInfoByIds(memberIds).stream()
				.collect(Collectors.toMap(u -> (long) u.getUserId(), Function.identity()));
		// 批次處理NOTIFY
		itemDao.addGroupItemNotifyBatch(groupId, memberIds, content, "itemlist",
				LocalDateTime.now(ZoneId.of("Asia/Taipei")));
		// ✅ 批次查未讀數（一次 DB 搞定）
		Map<Long, Integer> unreadMap = notifyDao.countUnreadByUserIds(memberIds).stream()
				.collect(Collectors.toMap(row -> ((Number) row[0]).longValue(), row -> ((Number) row[1]).intValue()));

		for (Long userId : memberIds) {

			UserInfo userInfo = userInfoMap.get(userId);
			if (userInfo == null)
				continue;

			if (userInfo.isNotifyByEmail()) {
				emailService.sendMailAsync(userInfo.getEmail(), "群組通知", content);
			}

			notifySocketService.pushUnreadCount(userId, unreadMap.getOrDefault(userId, 0));
		}
	}

public String store(MultipartFile image) {
    if (image == null || image.isEmpty())
        return null;
    try {
        String originalName = image.getOriginalFilename() == null ? "" : image.getOriginalFilename();
        String ext = "";
        int dotIdx = originalName.lastIndexOf('.');
        if (dotIdx >= 0) {
            ext = originalName.substring(dotIdx).toLowerCase();
        }

        List<String> allowedExt = List.of(".png", ".jpg", ".jpeg", ".gif", ".webp");
        if (!allowedExt.contains(ext)) {
            throw new IllegalArgumentException("不支援的檔案格式");
        }

        // 完全不用原始檔名，避免任何路徑穿越可能
        String fileName = UUID.randomUUID() + ext;

        Path uploadPath = Paths.get("/app/uploads");
        if (!Files.exists(uploadPath))
            Files.createDirectories(uploadPath);

        Files.copy(image.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + fileName;
    } catch (IllegalArgumentException e) {
        throw e;
    } catch (Exception e) {
        throw new RuntimeException("圖片上傳失敗", e);
    }
}

	public void validateImage(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			return;
		}

		// 例如限制 5MB
		long maxSize = 5 * 1024 * 1024;

		if (image.getSize() > maxSize) {
			throw new IllegalArgumentException("圖片大小不能超過 5MB");
		}

		String contentType = image.getContentType();

		if (contentType == null || !contentType.startsWith("image/")) {
			throw new IllegalArgumentException("只能上傳圖片檔案");
		}
	}
}
