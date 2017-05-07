package com.demo.app.hotel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

@Theme("mytheme")
public class HotelsUI extends UI {
	
	private HotelService hotelService = HotelService.getInstance();
	private CategoryService categoryService = CategoryService.getInstance();
	private Grid<Hotel> grid = new Grid<>(Hotel.class);
	private Grid<Category> categoryGrid = new Grid<>(Category.class);
	private VerticalLayout hotelRoot, categoryRoot;
    private HorizontalLayout gridLayout, toolbar, menuAndContent, categoryLayout;
    private CssLayout filterLayout, menu;
    private Panel description = new Panel("");
    private TextField filterAddress, filterName;
    private Button clearFilter, addHotel, hotelBtn, categoryBtn, createCategory;
    private HotelForm hotelForm = new HotelForm(this);
    private CategoryForm categoryForm = new CategoryForm(this);
    private Panel content = new Panel();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        configureHotelPage();
        configureCategoryPage();
    }

	private void configureCategoryPage() {
        setUpCategoryForms();
    	setUpCategoryGrid();
    	setUpCategoryLayouts();
	}

	private void configureHotelPage() {
        setUpHotelForms();
    	setUpHotelGrid();
    	setUpHotelLayouts();
	}

	private void setUpHotelLayouts() {
    	menu = new CssLayout();
    	menu.setStyleName("menu");
    	menu.setHeight("100%");
    	menu.addComponents(hotelBtn, categoryBtn);

		Label title = new Label("Hotels Manager");
		title.addStyleName("h1");
		title.addStyleName("bold");
		gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
		grid.setWidth("100%");
        gridLayout.setHeight("500");
        gridLayout.addComponents(grid, hotelForm);
        gridLayout.setExpandRatio(hotelForm,1);
        gridLayout.setExpandRatio(grid, 4);
	    filterLayout = new CssLayout();
		filterLayout.addComponents(filterName, filterAddress, clearFilter);
		filterLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		
		toolbar = new HorizontalLayout(filterLayout, addHotel);

		hotelRoot = new VerticalLayout();
		hotelRoot.addComponents(title, toolbar, gridLayout, description);
		hotelRoot.setWidth("100%");
       	menuAndContent = new HorizontalLayout();
       	menuAndContent.setSizeFull();
		menuAndContent.setSpacing(false);
    	menuAndContent.addComponents(menu, content);
    	menuAndContent.setExpandRatio(menu, 1);
		menuAndContent.setExpandRatio(content,14);


		hotelBtn.addStyleName("menubutton_clicked");
		content.setContent(hotelRoot);

		setContent(menuAndContent);
	}

	private void setUpHotelGrid() {

    	//Configure hotel list grid
		grid.setColumns("rating", "category", "name", "address", "operatesFrom");
		Grid.Column<Hotel, ?> ratingColumn = grid.getColumn("rating");
		ratingColumn.setRenderer(new StarsRenderer());
		Grid.Column<Hotel, ?> operatesColumn = grid.getColumn("operatesFrom");
		operatesColumn.setRenderer(new OperatesFromRenderer()).setCaption("Operates for");
        grid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>View at Booking.com</a>",
        		new HtmlRenderer()).setCaption("Link to Booking.com");
        grid.setSizeFull();
        updateHotelList();
        grid.addSelectionListener(e -> {
    		Set<Hotel> selected = e.getAllSelectedItems();
    		if (!selected.isEmpty()) {
    			Hotel sel = (Hotel) selected.toArray()[0];
    			String desc = sel.getDescription();
    			if (desc == null || desc.isEmpty())
    				description.setContent(new Label ("No Description"));
    			else
    				description.setContent(new Label(desc, ContentMode.PREFORMATTED));
    		}
       	});
        grid.asSingleSelect().addValueChangeListener(event -> {
        	if (event.getValue() == null) {
        		hotelForm.setVisible(false);
        	} else {
        		hotelForm.setHotel(event.getValue());
        	}
        });


	}

	private void setUpHotelForms() {
        hotelBtn = new Button("Hotels");
		hotelBtn.addStyleName("menubutton");
        hotelBtn.setIcon(FontAwesome.TASKS);
        hotelBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		hotelBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
		categoryBtn = new Button("Categories");
		categoryBtn.addStyleName("menubutton");
		categoryBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		categoryBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
		categoryBtn.setIcon(FontAwesome.TASKS);

		hotelBtn.addClickListener(event -> {
			categoryBtn.removeStyleName("menubutton_clicked");
		    hotelBtn.addStyleName("menubutton_clicked");
		    content.setContent(hotelRoot);
		});
		categoryBtn.addClickListener(event -> {
			hotelBtn.removeStyleName("menubutton_clicked");
			categoryBtn.addStyleName("menubutton_clicked");
			content.setContent(categoryRoot);
		});

		content.setSizeFull();
		content.addStyleName("content");
		description.setSizeFull();
		description.addStyleName("content");
		description.addStyleName("borderless");
		description.setHeight("100%");
		description.setWidth("100%");

		addHotel = new Button("Add new Hotel");
		addHotel.addClickListener(e -> {
			grid.asSingleSelect().clear();
			hotelForm.setHotel(new Hotel());
		});
		hotelForm.setVisible(false);
		
		filterAddress = new TextField();
		filterAddress.setPlaceholder("filter by address");
		filterAddress.addValueChangeListener(e -> updateHotelList());
		filterAddress.setValueChangeMode(ValueChangeMode.LAZY);;
		filterName = new TextField();
		filterName.setPlaceholder("filter by name");
		filterName.addValueChangeListener(e -> updateHotelList());
		filterName.setValueChangeMode(ValueChangeMode.LAZY);;
		
		clearFilter = new Button(VaadinIcons.CLOSE);
		clearFilter.setDescription("Clear the current filter");
		clearFilter.addClickListener(e -> {
			filterAddress.clear();
			filterName.clear();
		});

	}

	public void updateHotelList() {
		String addrFilter = filterAddress.getValue();
		String nameFilter = filterName.getValue();

		List<Hotel> hotelList = hotelService.findAll(nameFilter, addrFilter);
        grid.setItems(hotelList);
	}


	private void setUpCategoryLayouts() {
        categoryRoot = new VerticalLayout();
        Label title = new Label("Categories Manager");
        title.addStyleName("h1");
		title.addStyleName("bold");
		categoryRoot.addComponents(title, createCategory);
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

	private void setUpCategoryGrid() {
        //Configure category list grid
		categoryGrid.setColumns("name");
		categoryGrid.getColumn("name").setWidth(250);
		categoryGrid.addColumn(category -> {
			CategoryAndHotelConnector connector = new CategoryAndHotelConnector();
			Hotel current = connector.getHighestRatingHotel(category);
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
            }else {
                categoryForm.setCategories(selected);
                categoryForm.setVisible(true);
            }
        });
	}

	private void setUpCategoryForms() {
        createCategory = new Button("Create Category");
        createCategory.addClickListener(event -> {
            categoryGrid.asMultiSelect().clear();
            Set<Category> newSet = new HashSet<>();
            newSet.add(new Category("Default Name"));
            categoryForm.setCategories(newSet);
        });
		categoryForm.setVisible(false);
	}

	public void updateCategoryList() {
        Set<Category> categorySet = categoryService.findAllCategories();
        categoryGrid.setItems(categorySet);
        hotelForm.updateAvailableCategories();
	}

	public void clearGridSelection() {              //CategoryForm class uses this method when new category
        categoryGrid.asMultiSelect().clear();       //gets created, to clear previous selections
    }



    @WebServlet(urlPatterns = "/*", name = "HotelsUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = HotelsUI.class, productionMode = false)
    public static class HotelsUIServlet extends VaadinServlet {
    }
}
