package com.demo.app.hotel;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CategoryService {

    private static CategoryService instance;
    private CategoryAndHotelConnector connector = new CategoryAndHotelConnector();
    private static final Logger LOGGER = Logger.getLogger(CategoryService.class.getName());
    private final HashMap<Long, Category> categoryMap = new HashMap<>();
    private long nextId = 0;

    private CategoryService() { }

    public static CategoryService getInstance() {
        if (instance == null) {
            instance = new CategoryService();
            String[] initialCategories = { "Hotel", "Hostel", "GuestHouse", "Appartments" }; //add initial categories
            for (String s : initialCategories) {
                Category ht = new Category(s);
                instance.save(ht);
            }
        }
        return instance;
    }

    public synchronized void save(Category category) {
        if (category == null) {
            LOGGER.log(Level.SEVERE, "Category is null");
            return;
        }
        if (category.getId() == null) {
            category.setId(nextId++);
        }
        try {
            category = category.clone();
        } catch (CloneNotSupportedException e) {
            LOGGER.log(Level.SEVERE, "Clone not supported");
        }
        categoryMap.put(category.getId(), category);
        connector.synchronize(category);                //this method handles hotels update after category edit
    }

    public synchronized void delete(Category category) {
        categoryMap.remove(category.getId());
        connector.synchronize(category);                //this method handles hotels update after category delete
    }

    public synchronized Set<Category> findAllCategories() {
        Set<Category> result = new HashSet<>();         //HashSet has been used because it restricts having multiple
        try {                                           //categories with a same name
            for (Category c : categoryMap.values())
                result.add(c.clone());
        } catch (CloneNotSupportedException e) {
            LOGGER.log(Level.SEVERE, "Clone not supported");
        }
        return result;
    }

    public synchronized Category findOneCategory(Long id) {
        Category result = categoryMap.get(id);
        if (result == null) {
            return result;
        } else {
            try {
                return result.clone();
            } catch (CloneNotSupportedException e) {
                LOGGER.log(Level.SEVERE, "Clone not supported");
                return null;
            }
        }
    }
}
