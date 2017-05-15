package com.demo.app.hotel.backend.dao;

import com.demo.app.hotel.backend.entity.Category;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository("categoryDaoImpl")
public class CategoryDaoImpl implements CategoryDao {

	private EntityManager entityManager;

    public CategoryDaoImpl() { }

    public CategoryDaoImpl(EntityManager entityManager) {
    	this.entityManager = entityManager;
	}
	@Override
	public void save(Category entity) {
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
	public void delete(Category entity) {
		entityManager.getTransaction().begin();
		entityManager.remove(entity);
		entityManager.getTransaction().commit();
	}


	@Override
	public List<Category> findAll() {
	    List<Category> list;
	    list = entityManager.createQuery("FROM Category", Category.class).getResultList();
	    return list;
	}

	private void refresh(List<Category> list) {
	    for (Category c :list) {
			entityManager.getTransaction().begin();
	    	entityManager.refresh(c);
			entityManager.getTransaction().commit();
		}
	}
}
