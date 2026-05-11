package com.example.Family_life_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Family_life_backend.response.GetItemsRes;
import com.example.Family_life_backend.service.ItemsService;

@RestController
@RequestMapping(value = "/item")
@CrossOrigin(origins = "http://localhost:4200")
public class ItemsController {
	@Autowired
	private ItemsService itemsService;

	@GetMapping(value = "/getItems")
	public GetItemsRes getItems(@RequestParam("groupId") int groupId) {
		return itemsService.getItems(groupId);
	};

}
