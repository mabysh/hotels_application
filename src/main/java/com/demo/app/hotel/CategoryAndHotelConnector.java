package com.demo.app.hotel;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * The purpose of the class is to synchronize changes in categories with hotels containing them.
 */

public class CategoryAndHotelConnector {

    private List<Hotel> hotels;
    private Set<Category> categories;
    private HotelService hotelService;
    private CategoryService categoryService;

    public void synchronize(Category category) {
        hotelService = HotelService.getInstance();
        categoryService = CategoryService.getInstance();
        hotels = hotelService.findAll();
        categories = categoryService.findAllCategories();

        if (hotels.isEmpty()){  //no hotels
            return;
        }

        Category changed = categoryService.findOneCategory(category.getId()); //attempting to find this category
        if (changed == null) {                          //category has been deleted;
            if (categories.iterator().hasNext()) {      //there are some categories left;
                changed = categories.iterator().next(); //take first from the list;
                Long oldId = category.getId();
                Long newId = changed.getId();
                for (Hotel hotel : hotels) {            //edit hotels containing deleted category
                    if (hotel.getCategory().getId() == oldId) {
                        hotel.setCategory(categoryService.findOneCategory(newId));
                    }
                }
            } else {                                    //they deleted all categories! bastards...
                Category defaultCategory = new Category("Default Category");
                categoryService.save(defaultCategory);
                for (Hotel hotel : hotels)
                    hotel.setCategory(defaultCategory);
            }
        } else {                                        //category has been edited
            Long id = category.getId();
            for (Hotel hotel : hotels) {
                if (hotel.getCategory().getId() == id) {
                    hotel.setCategory(changed);
                }
            }
        }
    }

    public Hotel getHighestRatingHotel(Category category) {     //returns highest rating hotel for a category
        hotelService = HotelService.getInstance();
        categoryService = CategoryService.getInstance();
        hotels = hotelService.findAll();

        List<Hotel> filtered = hotels.stream().filter(h -> h.getCategory().equals(category))
                .collect(Collectors.toList());
        if (filtered.isEmpty())         //no hotels
            return new Hotel();
        else {
            Hotel highest = filtered.get(0);
            for (Hotel hotel : filtered) {
                if (hotel.getRating() > highest.getRating()) {
                    highest = hotel;
                }
            }
        return highest;
        }

    }
}
