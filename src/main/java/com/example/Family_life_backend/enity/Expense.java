package com.example.Family_life_backend.enity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;



@Entity
@Table(name = "expenses")
public class Expense {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(name = "group_id", nullable = false)
	private Long groupId;

	@NotNull(message = "使用者 ID 不能為空")
	@Column(name = "user_id", nullable = false)
	private Long userId;

	@NotBlank(message = "分類名稱不能為空白")
	@Column(nullable = false, length = 50, name = "category_id")
	private int categoryId;

	@Column(name = "related_item_id")
	private Long relatedItemId;

	@NotNull(message = "費用日期不能為空")
	@Column(name = "expense_date", nullable = false)
	private LocalDate expenseDate;

	@Column(columnDefinition = "TEXT", name = "note")
	private String note;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public Expense() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Expense(Long id, Long groupId, @NotNull(message = "使用者 ID 不能為空") Long userId,
			@NotBlank(message = "分類名稱不能為空白") @Size(max = 50, message = "分類名稱長度不能超過 50 個字") int categoryId,
			Long relatedItemId, @NotNull(message = "費用日期不能為空") LocalDate expenseDate, String note,
			LocalDateTime createdAt) {
		super();
		this.id = id;
		this.groupId = groupId;
		this.userId = userId;
		this.categoryId = categoryId;
		this.relatedItemId = relatedItemId;
		this.expenseDate = expenseDate;
		this.note = note;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public Long getRelatedItemId() {
		return relatedItemId;
	}

	public void setRelatedItemId(Long relatedItemId) {
		this.relatedItemId = relatedItemId;
	}

	public LocalDate getExpenseDate() {
		return expenseDate;
	}

	public void setExpenseDate(LocalDate expenseDate) {
		this.expenseDate = expenseDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}