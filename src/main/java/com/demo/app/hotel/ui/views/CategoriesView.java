package com.demo.app.hotel.ui.views;

import com.demo.app.hotel.backend.ApplicationService;
import com.demo.app.hotel.backend.Category;
import com.demo.app.hotel.backend.Hotel;
import com.demo.app.hotel.ui.forms.CategoryForm;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringComponent
@UIScope
@SpringView(name = CategoriesView.VIEW_NAME)
public class CategoriesView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "categories";
    private Button createCategory, deleteCategory, editCategory;
    private VerticalLayout categoryRoot;
    private HorizontalLayout categoryLayout, buttons;

    private Grid<Category> categoryGrid = new Grid<>(Category.class);

    private CategoryForm categoryForm = new CategoryForm(this);

    private ApplicationService service = ApplicationService.getInstance();

    @PostConstruct
    void init() {
        setUpCategoryForms();
    	setUpCategoryGrid();
    	setUpCategoryLayouts();
    	addComponent(categoryRoot);
    }

    private void setUpCategoryForms() {
        createCategory = new Button("New");
        createCategory.setStyleName(ValoTheme.BUTTON_PRIMARY);
        createCategory.addClickListener(event -> {
            categoryGrid.asMultiSelect().clear();
            Set<Category> newSet = new HashSet<>();
            newSet.add(new Category("Default Name"));
            categoryForm.setCategories(newSet);
            editCategory.setVisible(true);
            editCategory.setCaption("Save");
        });
        editCategory = new Button("Edit");
        editCategory.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        editCategory.addClickListener(e -> {
            categoryForm.save();
            editCategory.setVisible(false);
        });
        deleteCategory = new Button("Delete");
        deleteCategory.addClickListener(e -> categoryForm.delete());
		categoryForm.setVisible(false);
        editCategory.setVisible(false);
        deleteCategory.setVisible(false);
	}

	private void setUpCategoryGrid() {
        //Configure category list grid
		categoryGrid.setColumns("name");
		categoryGrid.getColumn("name").setWidth(250);
		categoryGrid.addColumn(category -> {
			Hotel current = service.getHighestRatingHotel(category);
			return current.getName();
		}).setCaption("Highest Rating Hotel");
		categoryGrid.setSizeFull();
		categoryGrid.setWidth("100%");
		categoryGrid.setSelectionMode(Grid.SelectionMode.MULTI);
		updateCategoryList();

		categoryGrid.asMultiSelect().addSelectionListener(event -> {
            Set<Category> selected = categoryGrid.getSelectedItems();
		    if (selected.isEmpty()) {
                categoryForm.setVisible(false);
                editCategory.setVisible(false);
                deleteCategory.setVisible(false);
            }else {
                deleteCategory.setVisible(true);
		        if (selected.size() == 1) {
		            editCategory.setCaption("Edit");
                    editCategory.setVisible(true);
                } else {
                    editCategory.setVisible(false);
                }
                categoryForm.setCategories(selected);
                categoryForm.setVisible(true);
            }
        });
	}

	private void setUpCategoryLayouts() {
        categoryRoot = new VerticalLayout();
        buttons = new HorizontalLayout(createCategory, editCategory, deleteCategory);
        Label title = new Label("Categories Manager");
        title.addStyleName("h1");
		title.addStyleName("bold");
		categoryRoot.addComponents(title, buttons);
		categoryRoot.setWidth("100%");
		categoryRoot.setHeight("100%");


		categoryLayout = new HorizontalLayout(categoryGrid, categoryForm);
		categoryLayout.setSizeFull();
        categoryLayout.setExpandRatio(categoryForm, 1);
        categoryLayout.setExpandRatio(categoryGrid, 3);
        categoryRoot.addComponent(categoryLayout);
        categoryRoot.setExpandRatio(title, 1);
        categoryRoot.setExpandRatio(categoryLayout, 10);

	}

	public void updateCategoryList() {
        List<Category> categories = (List<Category>) service.findAll(Category.class);
        categoryGrid.setItems(categories);
	}

	public void clearGridSelection() {              //CategoryForm class uses this method when new category
        categoryGrid.asMultiSelect().clear();       //gets created, to clear previous selections
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {   //update info on view change
        updateCategoryList();
    }
}
