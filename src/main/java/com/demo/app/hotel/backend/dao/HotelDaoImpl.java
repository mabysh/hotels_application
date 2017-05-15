package com.demo.app.hotel.backend.dao;

import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.Hotel;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository("hotelDaoImpl")
public class HotelDaoImpl implements HotelDao {

    private EntityManager entityManager;

    public HotelDaoImpl() { }

    public HotelDaoImpl(EntityManager entityManager) {
    	this.entityManager = entityManager;
	}

	@Override
	public void save(Hotel entity) {
		if (entity.isPersisted()) {
			entityManager.getTransaction().begin();
			entityManager.merge(entity);
			entityManager.getTransaction().commit();
		} else {
			entityManager.getTransaction().begin();
			entityManager.persist(entity);
			entityManager.getTransaction().commit();
		}
	}

	@Override
	public void delete(Hotel entity) {
		entityManager.getTransaction().begin();
		entityManager.remove(entity);
		entityManager.getTransaction().commit();
	}


	@Override
	public List<Hotel> findAll(String nameFilter, String addressFilter) {
		List<Hotel> list;
		if((nameFilter == null || nameFilter.isEmpty()) &&
           (addressFilter == null || addressFilter.isEmpty())) {
			list = entityManager.createQuery("FROM Hotel", Hotel.class).getResultList();
		} else {
			list = entityManager.createNamedQuery("Hotel.filter", Hotel.class).
                    setParameter("nameFilter", "%" + nameFilter + "%").
                    setParameter("addressFilter", "%" + addressFilter + "%").
                    getResultList();
		}
	    refresh(list);
	    return list;
	}

	public List<Hotel> findAll() {
		return findAll(null, null);
	}

	private void refresh(List<Hotel> list) {
	    for (Hotel h :list) {
			entityManager.getTransaction().begin();
	    	entityManager.refresh((h));
			entityManager.getTransaction().commit();
		}
	}

	@Override
	public synchronized List<Hotel> findByCategory(Category category) {
		List<Hotel> list;
        list = entityManager.createQuery("SELECT h from Hotel h where h.category=:category", Hotel.class)
                 .setParameter("category", category).getResultList();
        refresh(list);
        return list;
     }

}
