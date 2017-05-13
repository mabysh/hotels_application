package com.demo.app.hotel.backend;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationService {

    private static ApplicationService instance;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("demo_hotels");
    private EntityManager entityManager = emf.createEntityManager();

    public static ApplicationService getInstance() {
        if (instance == null) {
            instance = new ApplicationService();
            instance.ensureTestData();
        }
        return instance;
    }

    public synchronized void save(AbstractEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.merge(entity);
        entityManager.getTransaction().commit();
    }

    public synchronized void delete(AbstractEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

    public List<? extends AbstractEntity> findAll(Class<? extends AbstractEntity> theClass) {
        return findAll(null, null, theClass);
    }

    public List<? extends AbstractEntity> findAll(String nameFilter, String addressFilter,
                                        Class<? extends AbstractEntity> theClass) {
        if((nameFilter == null || nameFilter.isEmpty()) && (addressFilter == null || addressFilter.isEmpty())) {
            if (theClass.equals(Hotel.class)) {
				List<Hotel> list = entityManager.createQuery("FROM Hotel", Hotel.class).getResultList();
                refresh(list);
                return list;
            } else {
				return entityManager.createQuery("FROM Category", Category.class).getResultList();
            }
        } else {
            List<Hotel> list = entityManager.createNamedQuery("Hotel.filter", Hotel.class).
					setParameter("nameFilter", "%" + nameFilter + "%").
					setParameter("addressFilter", "%" + addressFilter + "%").
					getResultList();
            refresh(list);
            return list;
        }
    }

    private synchronized List<Hotel> findByCategory(Category category) {
		return entityManager.createQuery("SELECT h from Hotel h where h.category=:category", Hotel.class)
				.setParameter("category", category).getResultList();
	}

	private void refresh(List<Hotel> list) {
        for (Hotel h : list ) {
            entityManager.getTransaction().begin();
            entityManager.refresh(h);
            entityManager.getTransaction().commit();
        }
    }

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

    private void ensureTestData() {
		if (findAll(Hotel.class).isEmpty()) {
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
			List<Category> categories = (List<Category>) findAll(Category.class);
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
				save(h);
			}
		}
	}
}
