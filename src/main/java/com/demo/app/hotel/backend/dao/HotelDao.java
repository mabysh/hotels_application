package com.demo.app.hotel.backend.dao;

import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.Hotel;

import java.util.List;

public interface HotelDao {
	void save(Hotel entity);
    void delete(Hotel entity);
    List<Hotel> findAll(String nameFilter, String addressFilter);
    List<Hotel> findByCategory(Category category);
}