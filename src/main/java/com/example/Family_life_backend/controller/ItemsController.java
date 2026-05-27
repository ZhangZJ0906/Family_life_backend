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

import com.example.Family_life_backend.request.ItemAddInfoReq;
import com.example.Family_life_backend.request.ItemUpdateReq;
import com.example.Family_life_backend.response.AddItemsInfoRes;
import com.example.Family_life_backend.response.BasicRes;
import com.example.Family_life_backend.response.GetItemsRes;
import com.example.Family_life_backend.service.ItemsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/item")
@CrossOrigin(origins = "http://localhost:4200")
public class ItemsController {
	
	@Autowired
	private ItemsService itemsService;

	@GetMapping("/getItems")
	public GetItemsRes getItems(
	        @RequestParam("userId") Integer userId,
	        @RequestParam(value = "groupId", required = false) Integer groupId) {

	    System.out.println("UID:" + userId);
	    System.out.println("GID:" + groupId);

	    return itemsService.getItems(groupId, userId);
	}
	@PostMapping("/add")
	public AddItemsInfoRes addItem(@Valid @RequestBody ItemAddInfoReq req) {

		return itemsService.saveItem(req);
	}

	@PostMapping("/update")
	public BasicRes updateItem(@Valid @RequestBody ItemUpdateReq req) {

		return itemsService.updateItem(req);
	}

	@PostMapping("/delete")
	public BasicRes deleteItem(@RequestBody List<Integer> id) {

		return itemsService.deleteItem(id);
	}
}
