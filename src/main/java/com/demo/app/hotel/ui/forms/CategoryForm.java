package com.demo.app.hotel.ui.forms;

import com.demo.app.hotel.backend.ApplicationService;
import com.demo.app.hotel.backend.Category;
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

    private ApplicationService service = ApplicationService.getInstance();
    private Set<Category> categories;
    private CategoriesView view;
    private Binder<Category> binder = new Binder<>(Category.class);

    public CategoryForm(CategoriesView view) {
        this.view = view;
        configureComponents();
    }

    private void configureComponents() {
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
        if (categories.size() == 1) {
            name.setVisible(true);
            name.selectAll();
            multiple.setVisible(false);
        } else {
            name.setVisible(false);
            multiple.setVisible(true);
        }
        setVisible(true);
    }

    public void delete() {
        for (Category hc : categories) {
            service.delete(hc);
        }
        view.updateCategoryList();        // <-- this method updates category list in grid on delete;
        setVisible(false);
        Notification.show("Selected categories has been deleted");
    }

    public void save() {
        try {
            for (Category hc : categories) {
                binder.writeBean(hc);
                service.save(hc);
            }
        } catch (ValidationException ex) {
            view.clearGridSelection();
            Notification.show("Category could not be saved. Please, enter valid name");
            return;
        }
       view.updateCategoryList();        // <-- this method updates category list in grid on save
        setVisible(false);
        Notification.show("Categories has been updated");
    }
}
