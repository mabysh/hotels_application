package com.demo.app.hotel;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.HashSet;
import java.util.Set;

public class CategoryForm extends FormLayout {

    private Label multiple = new Label("Multiple items selected. \nDelete them or create new one.", ContentMode.PREFORMATTED);
    private TextField name = new TextField("Name");
    private Button newBtn = new Button("New");
    private Button edit = new Button("Edit");
    private Button delete = new Button("Delete");

    private CategoryService categoryService = CategoryService.getInstance();
    private Set<Category> categories;
    private HotelsUI ui;
    private Binder<Category> binder = new Binder<>(Category.class);

    public CategoryForm(HotelsUI ui) {
        this.ui = ui;
        configureComponents();
    }

    private void configureComponents() {
        setSizeUndefined();
        HorizontalLayout buttons = new HorizontalLayout(newBtn, edit, delete);

        name.setRequiredIndicatorVisible(true);
        name.setDescription("Category Name");
        binder.forField(name).asRequired("Enter category name to proceed")
                .withValidator(new RegexpValidator("Incorrect name!", "[0-9a-zA-Z ]*+"))
                .bind(Category::getName, Category::setName);

        newBtn.addClickListener(e -> {
            ui.clearGridSelection();
            HashSet<Category> newSet = new HashSet<>();
            newSet.add(new Category("Default Name"));
            setCategories(newSet);
        });
        newBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        delete.addClickListener(e -> delete());
        edit.addClickListener(e -> save());

        addComponents(name, multiple, buttons);
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
        for (Category hc : categories) {
            binder.readBean(hc);
        }
        edit.setCaption("Edit");
        if (categories.size() == 1) {
            edit.setVisible(true);
            name.setVisible(true);
            name.selectAll();
            multiple.setVisible(false);
            //In case of creation new item, change button caption to "Save" instead of "Edit"
            if (!categories.toArray(new Category[1])[0].isPersisted()) {
                edit.setCaption("Save");
            }
        } else {
            edit.setVisible(false);
            name.setVisible(false);
            multiple.setVisible(true);
        }
        setVisible(true);
    }

    private void delete() {
        for (Category hc : categories) {
            categoryService.delete(hc);
        }
        ui.updateCategoryList();        // <-- this method updates category lists both in grid and Hotel Form on delete;
        ui.updateHotelList();           // <-- this method update Hotels grid with changed categories;
        setVisible(false);
        Notification.show("Selected categories has been deleted");
    }

    private void save() {
        try {
            for (Category hc : categories) {
                binder.writeBean(hc);
                categoryService.save(hc);
            }
        } catch (ValidationException ex) {
            ui.clearGridSelection();
            Notification.show("Category could not be saved. Please, enter valid name");
            return;
        }
        ui.updateCategoryList();        // <-- this method updates category lists both in grid and Hotel Form on save
        ui.updateHotelList();           // <-- this method update Hotels grid with changed categories;
        setVisible(false);
        Notification.show("Categories has been updated");
    }
}
