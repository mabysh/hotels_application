package com.demo.app.hotel.ui.views;

import com.demo.app.hotel.backend.service.ApplicationService;
import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.Hotel;
import com.demo.app.hotel.backend.service.ServiceFactory;
import com.demo.app.hotel.ui.forms.CategoryForm;
import com.vaadin.event.ShortcutAction;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;

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

    private ApplicationService service = ServiceFactory.getApplicationServiceImpl();

    private boolean isViewCreated = false;

    @PostConstruct
    void init() {
        setUpCategoryForms();
    	setUpCategoryGrid();
    	setUpCategoryLayouts();
    	addComponent(categoryRoot);
    }

    private void setUpCategoryForms() {
        createCategory = new Button(VaadinIcons.PLUS);
        createCategory.setDescription("Create new category");
        createCategory.addStyleName("create_category");
        createCategory.addClickListener(event -> {
            clearGridSelection();
            Set<Category> newSet = new HashSet<>();
            newSet.add(new Category(""));
            categoryForm.setCategories(newSet);
            manageButtons(true, false);
        });
        editCategory = new Button(VaadinIcons.CHECK);
        editCategory.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        editCategory.setDescription("Save/Update category");
        editCategory.addStyleName("edit_category");
        editCategory.addClickListener(e -> categoryForm.save() );
        deleteCategory = new Button(VaadinIcons.TRASH);
        deleteCategory.setDescription("Delete category");
        deleteCategory.addClickListener(e -> categoryForm.delete());
        deleteCategory.addStyleName("delete_category");
		categoryForm.setVisible(false);
		manageButtons(false, false);
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
		    if (event == null) {
                categoryForm.setVisible(false);
		        manageButtons(false, false);
		        return;
            }
            Set<Category> selected = categoryGrid.getSelectedItems();
		    int size = selected.size();
		    manageButtons(size == 1,
                size > 0 && selected.iterator().next().isPersisted());
            categoryForm.setCategories(selected);
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
        List<Category> categories = service.findAllCategories();
        categoryGrid.setItems(categories);
	}

	public void clearGridSelection() {              //CategoryForm class uses this method when new category
        categoryGrid.asMultiSelect().clear();       //gets created, to clear previous selections
    }

    public void setInitialState() {
        List<Category> categories = service.findAllCategories();
        categoryGrid.setItems(categories);
        categoryForm.setVisible(false);
        clearGridSelection();
        manageButtons(false, false);
    }

    private void manageButtons(boolean editButton, boolean deleteButton) {
        editCategory.setEnabled(editButton);
        deleteCategory.setEnabled(deleteButton);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        if (!isViewCreated) {		//do not redraw components on each enter
            init();
            isViewCreated = true;
        }
        updateCategoryList();       //update info on view change
    }
}
