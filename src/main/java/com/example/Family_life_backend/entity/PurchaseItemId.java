package com.example.Family_life_backend.entity;

import java.io.Serializable;
import java.util.Objects;

public class PurchaseItemId implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;

	private int listId;

	public PurchaseItemId() {
	}

	public PurchaseItemId(int id, int listId) {
		this.id = id;
		this.listId = listId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getListId() {
		return listId;
	}

	public void setListId(int listId) {
		this.listId = listId;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof PurchaseItemId)) {
			return false;
		}
		PurchaseItemId other = (PurchaseItemId) obj;
		return id == other.id && listId == other.listId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, listId);
	}

}
