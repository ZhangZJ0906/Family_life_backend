package com.example.Family_life_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.entity.PurchaseItem;
import com.example.Family_life_backend.request.AddPurchaseItemReq;
import com.example.Family_life_backend.request.CreateListReq;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.service.ShoppingListService;

import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/shopping_lists")
public class ShoppingListController {

	@Autowired
	private ShoppingListService shoppingListService;

	/* 新增購物清單 */
	@PostMapping("/create")
	public BasicRes create(@Valid @RequestBody CreateListReq req) {
		return shoppingListService.create(req);
	}

	/* 刪除購物清單 */
	@PostMapping("/delete")
	public BasicRes delete(@RequestParam("listId") int listId) {
		return shoppingListService.delete(listId);
	}

	/* 變更購物清單 */
	@PostMapping("/update")
	public BasicRes updateList(@RequestBody CreateListReq req) {
		return shoppingListService.updateList(req);
	}

	/* 查看購物項目 */
	@GetMapping("/items")
	public List<PurchaseItem> getItems(@RequestParam ("listId") int listId) {
		return shoppingListService.getItems(listId);
	}

	/* 在已有的購物清單裡增加購物項目 */
	@PostMapping("/items/add")
	public BasicRes addItems(@Valid @RequestBody AddPurchaseItemReq req) {
		return shoppingListService.addItems(req);
	}

	/* 刪除購物清單裡的購物項目 */
	@PostMapping("/items/delete")
	public BasicRes deleteItem(@RequestParam("listId") int listId,
			@RequestParam("itemId") int itemId) {
		return shoppingListService.deleteItem(listId, itemId);
	}

	/* 勾選 / 取消勾選購物項目 */
	@PostMapping("/items/check")
	public BasicRes updateCheck(@RequestParam("listId") int listId,
			@RequestParam("itemId") int itemId,
			@RequestParam("check") boolean check,
			@RequestParam("checkMan") int checkMan) {
		return shoppingListService.updateCheck(listId, itemId, check, checkMan);
	}

}
