package com.demo.app.hotel.backend.dao;

import com.demo.app.hotel.backend.entity.Category;

import java.util.List;

public interface CategoryDao {
	void save(Category entity);
    void delete(Category entity);
    List<Category> findAll();

}