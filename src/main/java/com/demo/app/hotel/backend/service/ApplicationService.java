package com.demo.app.hotel.backend.service;

import com.demo.app.hotel.backend.entity.AbstractEntity;
import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.GuaranteeFee;
import com.demo.app.hotel.backend.entity.Hotel;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

@Repository
public class ApplicationService {

    @PersistenceContext(unitName = "demo_hotels")
    private EntityManager entityManager;
    
    private int categoriesFetched;
    private int hotelsFetched;

    public ApplicationService() {
    }
    			
    @Transactional 
    public List<Hotel> findAllHotels() {
    	return entityManager.createQuery("FROM Hotel", Hotel.class).getResultList();
    }

    @Transactional
	public List<Hotel> findAllHotels(List<String> filter) {
		List<Hotel> list;
		String nameFilter = filter.get(0);
		String addressFilter = filter.get(1);
		if((nameFilter == null || nameFilter.isEmpty()) &&
           (addressFilter == null || addressFilter.isEmpty())) {
			list = entityManager.createQuery("FROM Hotel", Hotel.class).getResultList();
		} else {
			list = entityManager.createNamedQuery("Hotel.filter", Hotel.class).
                    setParameter("nameFilter", "%" + nameFilter + "%").
                    setParameter("addressFilter", "%" + addressFilter + "%")
                    .getResultList();
		}
		hotelsFetched = list.size();
	    return list;
	}
    
    @Transactional
    public List<Hotel> refreshHotels(List<Hotel> list) {
    	for (Hotel h : list) {
    		entityManager.refresh(h);
    	}
    	return list;
    }

	@Transactional
	public List<Category> findAllCategories() {
		List<Category> list;
	    list = entityManager.createQuery("FROM Category", Category.class).getResultList();
	    categoriesFetched = list.size();
	    return list;

	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(AbstractEntity entity) {
		if (entity.isPersisted()) {
			entityManager.merge(entity);
		} else {
			entityManager.persist(entity);
		}
	}
	
	    @Transactional(propagation = Propagation.REQUIRED)
	public void delete(AbstractEntity entity) {
		entityManager.remove(entityManager.contains(entity) ? 	//checks whether entity within current
				entity : entityManager.merge(entity));			//transaction and merge it if it's not
	}

	@Transactional
	public List<Hotel> findByCategory(Category category) {
		List<Hotel> list;
        list = entityManager.createQuery("SELECT h from Hotel h where h.category=:category", Hotel.class)
                 .setParameter("category", category).getResultList();
        return list;
     }

	@Transactional
	public Hotel getHighestRatingHotel(Category category) {     //returns highest rating hotel for a category

         List<Hotel> filtered = findByCategory(category);
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
	
	public int getHotelsFentched() { return hotelsFetched; }
	public int getCategoriesFetched() {
		return categoriesFetched;
	}

	@Transactional(propagation = Propagation.REQUIRED)
    public void ensureTestData() {
		if (findAllHotels().isEmpty() || findAllCategories() == null) {
			final String[] hotelData = new String[] {
					"3 Nagas Luang Prabang - MGallery by Sofitel;4;https://www.booking.com/hotel/la/3-nagas-luang-prabang-by-accor.en-gb.html;Vat Nong Village, Sakkaline Road, Democratic Republic Lao, 06000 Luang Prabang, Laos;",
					"Abby Boutique Guesthouse;1;https://www.booking.com/hotel/la/abby-boutique-guesthouse.en-gb.html;Ban Sawang , 01000 Vang Vieng, Laos",
					"Bountheung Guesthouse;1;https://www.booking.com/hotel/la/bountheung-guesthouse.en-gb.html;Ban Tha Heua, 01000 Vang Vieng, Laos",
					"Chalouvanh Hotel;2;https://www.booking.com/hotel/la/chalouvanh.en-gb.html;13 road, Ban Phonesavanh, Pakse District, 01000 Pakse, Laos",
					"Chaluenxay Villa;3;https://www.booking.com/hotel/la/chaluenxay-villa.en-gb.html;Sakkarin Road Ban Xienthong Luang Prabang Laos, 06000 Luang Prabang, Laos",
					"Dream Home Hostel 1;1;https://www.booking.com/hotel/la/getaway-backpackers-hostel.en-gb.html;049 Sihome Road, Ban Sihome, 01000 Vientiane, Laos",
					"Inpeng Hotel and Resort;2;https://www.booking.com/hotel/la/inpeng-and-resort.en-gb.html;406 T4 Road, Donekoy Village, Sisattanak District, 01000 Vientiane, Laos",
					"Jammee Guesthouse II;2;https://www.booking.com/hotel/la/jammee-guesthouse-vang-vieng1.en-gb.html;Vang Vieng, 01000 Vang Vieng, Laos",
					"Khemngum Guesthouse 3;2;https://www.booking.com/hotel/la/khemngum-guesthouse-3.en-gb.html;Ban Thalat No.10 Road Namngum Laos, 01000 Thalat, Laos",
					"Khongview Guesthouse;1;https://www.booking.com/hotel/la/khongview-guesthouse.en-gb.html;Ban Klang Khong, Khong District, 01000 Muang Không, Laos",
					"Kong Kham Pheng Guesthouse;1;https://www.booking.com/hotel/la/kong-kham-pheng-guesthouse.en-gb.html;Mixay Village, Paksan district, Bolikhamxay province, 01000 Muang Pakxan, Laos",
					"Laos Haven Hotel & Spa;3;https://www.booking.com/hotel/la/laos-haven.en-gb.html;047 Ban Viengkeo, Vang Vieng , 01000 Vang Vieng, Laos",
					"Lerdkeo Sunset Guesthouse;1;https://www.booking.com/hotel/la/lerdkeo-sunset-guesthouse.en-gb.html;Muang Ngoi Neua,Ban Ngoy-Nua, 01000 Muang Ngoy, Laos",
					"Luangprabang River Lodge Boutique 1;3;https://www.booking.com/hotel/la/luangprabang-river-lodge.en-gb.html;Mekong River Road, 06000 Luang Prabang, Laos",
					"Manichan Guesthouse;2;https://www.booking.com/hotel/la/manichan-guesthouse.en-gb.html;Ban Pakham Unit 4/143, 60000 Luang Prabang, Laos",
					"Mixok Inn;2;https://www.booking.com/hotel/la/mixok-inn.en-gb.html;188 Sethathirate Road , Mixay Village , Chanthabuly District, 01000 Vientiane, Laos",
					"Ssen Mekong;2;https://www.booking.com/hotel/la/muang-lao-mekong-river-side-villa.en-gb.html;Riverfront, Mekong River Road, 06000 Luang Prabang, Laos",
					"Nammavong Guesthouse;2;https://www.booking.com/hotel/la/nammavong-guesthouse.en-gb.html;Ban phone houang Sisalearmsak Road , 06000 Luang Prabang, Laos",
					"Niny Backpacker hotel;1;https://www.booking.com/hotel/la/niny-backpacker.en-gb.html;Next to Wat Mixay, Norkeokhunmane Road., 01000 Vientiane, Laos",
					"Niraxay Apartment;2;https://www.booking.com/hotel/la/niraxay-apartment.en-gb.html;Samsenthai Road Ban Sihom , 01000 Vientiane, Laos",
					"Pakse Mekong Hotel;2;https://www.booking.com/hotel/la/pakse-mekong.en-gb.html;No 062 Khemkong Road, Pakse District, Champasak, Laos, 01000 Pakse, Laos",
					"Phakchai Hotel;2;https://www.booking.com/hotel/la/phakchai.en-gb.html;137 Ban Wattay Mueng Sikothabong Vientiane Laos, 01000 Vientiane, Laos",
					"Phetmeuangsam Hotel;2;https://www.booking.com/hotel/la/phetmisay.en-gb.html;Ban Phanhxai, Xumnuea, Xam Nua, 01000 Xam Nua, Laos" };

			final String[] initialCategories = { "Hotel", "Hostel", "GuestHouse", "Appartments" };
			for (String s : initialCategories) {
                Category ht = new Category(s);
                save(ht);
            }

			Random r = new Random(0);
			//Retrieve all initial categories here
			List<Category> categories = findAllCategories();
			for (String hotel : hotelData) {
				String[] split = hotel.split(";");
				Hotel h = new Hotel();
				h.setName(split[0]);
				try {
					int rat = Integer.parseInt(split[1]);
					h.setRating(rat);
				} catch (NumberFormatException e) {
					Logger.getLogger(ApplicationService.class.getName()).log(Level.SEVERE, null, e);
				}
				h.setUrl(split[2]);
				h.setAddress(split[3]);
				h.setCategory((Category) categories.toArray()[r.nextInt(categories.size())]);
				int daysOld = r.nextInt(365 * 30);
				h.setOperatesFrom((long) daysOld);
				GuaranteeFee f = new GuaranteeFee();
				h.setGuaranteeFee(f);
				save(h);
			}
		}
	}
}
