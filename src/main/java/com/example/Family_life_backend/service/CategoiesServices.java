package com.example.Family_life_backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Family_life_backend.dao.CategoiesDao;
import com.example.Family_life_backend.enity.Categories;
import com.example.Family_life_backend.response.GetCatgoiesRes;

@Service
public class CategoiesServices {
	@Autowired
	private CategoiesDao categoiesDao;

	public GetCatgoiesRes getall() {
		List<Categories> list = categoiesDao.getItemCategoriesList();
		if (list.isEmpty() || list == null) {
			return new GetCatgoiesRes("錯誤", 400);
		}
		Map<Integer, String> map = list.stream().collect(Collectors.toMap(

				Categories::getCategoryId, Categories::getCategoryName, (existing, replace) -> existing));
		return new GetCatgoiesRes("成功", 200, map);
	}
}
