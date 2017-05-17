package com.demo.app.hotel.backend.service;

import com.demo.app.hotel.backend.entity.AbstractEntity;
import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.Hotel;

import java.util.List;

public interface ApplicationService {

    void save(AbstractEntity entity);
    void delete(AbstractEntity entity);
    List<Hotel> findAllHotels(String nameFilter, String addressFilter);
    List<Category> findAllCategories();
}
