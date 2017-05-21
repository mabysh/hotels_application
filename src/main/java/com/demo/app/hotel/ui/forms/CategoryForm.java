package com.demo.app.hotel.ui.forms;

import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.service.ServiceFactory;
import com.demo.app.hotel.ui.views.CategoriesView;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

import java.util.Set;

public class CategoryForm extends FormLayout {

    private Label multiple = new Label("Multiple items selected. \nDelete them or create new one.", ContentMode.PREFORMATTED);
    private TextField name = new TextField("Name");

    private Set<Category> categories;
    private CategoriesView view;
    private Binder<Category> binder = new Binder<>(Category.class);

    public CategoryForm(CategoriesView view) {
        this.view = view;
        setUpComponents();
    }

    private void setUpComponents() {
        setSizeUndefined();

        name.setRequiredIndicatorVisible(true);
        name.setDescription("Category Name");
        binder.forField(name).asRequired("Enter category name to proceed")
                .withValidator(new RegexpValidator("Incorrect name!", "[0-9a-zA-Z ]*+"))
                .bind(Category::getName, Category::setName);

        addComponents(name, multiple);
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
        for (Category hc : categories) {
            binder.readBean(hc);
        }
        int size = categories.size();
        manageForm(size == 1, size > 1);
        setVisible(!categories.isEmpty());
    }

    public void delete() {
        for (Category hc : categories) {
            ServiceFactory.getApplicationServiceImpl().delete(hc);
            binder.removeBean();
        }
        view.setInitialState();
        Notification.show("Selected categories has been deleted");
    }

    public void save() {
    	if (binder.hasChanges()) {
    		try {
    			for (Category hc : categories) {
    				binder.writeBean(hc);
    				ServiceFactory.getApplicationServiceImpl().save(hc);
    				binder.removeBean();
    			}
    		} catch (ValidationException ex) {
    			view.clearGridSelection();
    			Notification.show("Category could not be saved. Please, enter valid name");
    			return;             //form stays opened on invalid input
    		}
    		view.setInitialState();
    		Notification.show("Categories has been updated");
    	} else {
    		Notification.show("No changes have been made");
    	}
    }

    private void manageForm(boolean nameEnable, boolean multipleEnable) {
        name.setVisible(nameEnable);
        multiple.setVisible(multipleEnable);
    }
}
