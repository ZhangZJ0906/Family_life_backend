package com.example.Family_life_backend.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.Family_life_backend.entity.PurchaseItem;
import com.example.Family_life_backend.entity.PurchaseItemId;

import jakarta.transaction.Transactional;

public interface PurchaseItemDao extends JpaRepository<PurchaseItem, PurchaseItemId> {

	/* 查看購物項目 */
	@Query(value = "select * from shopping_list_items where shopping_list_id = ?1", nativeQuery = true)
	public List<PurchaseItem> getByListId(int listId);

	/* 取得該購物清單目前最大的項目 id */
	@Query(value = "select coalesce(max(id), 0) from shopping_list_items where shopping_list_id = ?1", nativeQuery = true)
	public int getMaxIdByListId(int listId);
	
	/* 刪除購物清單底下的購物項目 */
	@Modifying
	@Transactional
	@Query(value = "delete from shopping_list_items where shopping_list_id = ?1", nativeQuery = true)
	public void deleteByListId(int listId);

	/* 勾選 / 取消勾選購物項目 */
	@Modifying
	@Transactional
	@Query(value = "update shopping_list_items set"
			+ " is_checked = ?3, checked_at = ?4, checked_by_id = ?5 where shopping_list_id = ?1 and id = ?2", nativeQuery = true)
	public void updateCheck(int listId, int itemId, boolean check, LocalDate checkDate, int checkMan);
	
	//發送通知
	@Modifying
	@Transactional
	@Query(value = """
			    insert into notify (send_id, get_user_id, content, type, is_read)
			    values (:sendId, :getUserId, :content, :type, :isRead)
			""", nativeQuery = true)
	public void sendPurchaseReqToAnotherNotify(@Param("sendId") Long sendId, @Param("getUserId") Long getUserId,
			@Param("content") String content, @Param("type") String type, @Param("isRead") boolean isRead);
	
	//拿刪除指定Item名字
	@Query(value = "select item_name from shopping_list_items where id = :ItemId and shopping_list_id = :listId", nativeQuery = true)
	public String getItemNameById(@Param("listId") Long ListId, @Param("ItemId") Long ItemId);

}
