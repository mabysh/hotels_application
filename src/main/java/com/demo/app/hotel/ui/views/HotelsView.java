package com.demo.app.hotel.ui.views;

import com.demo.app.hotel.backend.entity.Hotel;
import com.demo.app.hotel.ui.forms.BulkForm;
import com.demo.app.hotel.ui.forms.HotelForm;
import com.demo.app.hotel.ui.representation.CategoryRenderer;
import com.demo.app.hotel.backend.service.HotelDataProvider;
import com.demo.app.hotel.ui.representation.OperatesFromRenderer;
import com.demo.app.hotel.ui.representation.StarsRenderer;
import com.vaadin.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;


import javax.annotation.PostConstruct;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
@SpringComponent
@UIScope
@SpringView(name = HotelsView.VIEW_NAME)
public class HotelsView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "hotels";

	private Button addHotel, deleteHotel, saveHotel, bulk;
	private TextField filterAddress;
	private Grid<Hotel> grid = new Grid<>(Hotel.class);
	private CssLayout filterLayout;
	private HorizontalLayout gridLayout;
	private HotelForm hotelForm = new HotelForm(this);
	private HorizontalLayout toolbar;
	private VerticalLayout hotelRoot;
	private TextField filterName;
	private Button clearFilter;
	private Panel description = new Panel("");
	private BulkForm popupContent = new BulkForm(this);
	private PopupView popup = new PopupView(null, popupContent);
	private final Label multiple = new Label("Multiple hotels selected. " +
			"Available options: create new hotel, delete selected, bulk update selected.");
	private final Label noDesc = new Label("No Description");
	
	private ConfigurableFilterDataProvider<Hotel, Void, List<String>> dataProvider;

	private boolean isViewCreated = false;

	@PostConstruct
    public void init() {
        setUpHotelForms();
    	setUpHotelGrid();
    	setUpHotelLayouts();
		addComponents(hotelRoot);
    }

    private void setUpHotelForms() {

		description.setSizeFull();
		description.addStyleName("content");
		description.addStyleName("borderless");
		description.setHeight("100%");
		description.setWidth("100%");

		addHotel = new Button(VaadinIcons.PLUS);
		addHotel.setDescription("Create new hotel");
		addHotel.addStyleName("add_hotel");
		addHotel.addClickListener(e -> {
			grid.asMultiSelect().clear();
			Set<Hotel> newSet = new HashSet<>();
			newSet.add(new Hotel());
			hotelForm.setHotels(newSet);
			manageButtons(true, true, false, false);
		});
		deleteHotel = new Button(VaadinIcons.TRASH);
		deleteHotel.setDescription("Delete selected hotels");
		deleteHotel.addClickListener(e -> hotelForm.delete());
		deleteHotel.addStyleName("delete_hotel");
		saveHotel = new Button(VaadinIcons.CHECK);
		saveHotel.setDescription("Save/Edit single hotel");
		saveHotel.addClickListener(e -> hotelForm.save());
		saveHotel.addStyleName("save_hotel");

		bulk = new Button("Bulk Update");
		bulk.setDescription("Bulk Update");
		bulk.addClickListener(e -> popup.setPopupVisible(true));

		popup.setHideOnMouseOut(false);
		popup.addPopupVisibilityListener(e -> {
			if (e.isPopupVisible()) {
				manageButtons(false, false, false, false);
			} else {
				grid.asMultiSelect().clear();
			}
		});
		popup.setSizeFull();

		filterAddress = new TextField();
		filterAddress.setPlaceholder("filter by address");
		filterAddress.addValueChangeListener(e -> updateHotelList());
		filterAddress.setValueChangeMode(ValueChangeMode.LAZY);
		filterName = new TextField();
		filterName.setPlaceholder("filter by name");
		filterName.addValueChangeListener(e -> updateHotelList());
		filterName.setValueChangeMode(ValueChangeMode.LAZY);

		clearFilter = new Button(VaadinIcons.CLOSE);
		clearFilter.setDescription("Clear the current filter");
		clearFilter.addStyleName("clear_filter");
		clearFilter.addClickListener(e -> {
			filterAddress.clear();
			filterName.clear();
		});

		hotelForm.setSizeUndefined();
		hotelForm.setVisible(false);
		manageButtons(true, false, false, false);
	}

	public void hidePopup() {		//used from BulkUpdate to hide popup on button click
		popup.setPopupVisible(false);
		popupContent.setHotels(new HashSet<Hotel>());
	}

	private void setUpHotelGrid() {

    	//Configure hotel list grid
		dataProvider = new HotelDataProvider().withConfigurableFilter();
		grid.setDataProvider(dataProvider);

		grid.setColumns("rating", "category", "name", "address", "operatesFrom");
		grid.getColumn("category").setRenderer(new CategoryRenderer()).setCaption("Category");
		grid.getColumn("rating").setRenderer(new StarsRenderer());
		grid.getColumn("operatesFrom").setRenderer(new OperatesFromRenderer()).setCaption("Operates for");
        grid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_blank'>View at Booking.com</a>",
        		new HtmlRenderer()).setCaption("Link to Booking.com");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.setSizeFull();
        updateHotelList();
        grid.asMultiSelect().addSelectionListener(e -> {
            if (e == null) {
            	hotelForm.setVisible(false);
            	multiple.setVisible(false);
         	   	manageButtons(true,false, false, false);
            	return;
			}
    		Set<Hotel> selected = e.getAllSelectedItems();
			hotelForm.setHotels(selected);
			manageForms(selected);
       	});
	}

	private void manageForms(Set<Hotel> selected) {
		int size = selected.size();
		manageButtons(true, size == 1, size > 0, size > 1);
		if (size == 1) {
			Hotel sel = selected.iterator().next();
			String desc = sel.getDescription();
    			if (desc == null || desc.isEmpty())
    				description.setContent(noDesc);
    			else
    				description.setContent(new Label(desc, ContentMode.PREFORMATTED));
		} else if (size == 0){
			description.setContent(noDesc);
			hotelForm.setVisible(false);
		} else {
		    popupContent.setHotels(selected);
			hotelForm.setVisible(false);
			description.setContent(multiple);
		}
	}

	private void setUpHotelLayouts() {

		Label title = new Label("Hotels Manager");
		title.addStyleName("h1");
		title.addStyleName("bold");
		gridLayout = new HorizontalLayout();
        gridLayout.setWidth("100%");
        gridLayout.setHeight("600");
        gridLayout.addComponents(grid, hotelForm);
        gridLayout.setExpandRatio(grid, 35);
	    filterLayout = new CssLayout();
		filterLayout.addComponents(filterName, filterAddress, clearFilter);
		filterLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

		toolbar = new HorizontalLayout(filterLayout, addHotel, saveHotel, deleteHotel, bulk);

		hotelRoot = new VerticalLayout();
		hotelRoot.addComponents(title, toolbar, popup, gridLayout, description);
		hotelRoot.setComponentAlignment(popup, Alignment.MIDDLE_CENTER);
		hotelRoot.setWidth("100%");
	}

	public void updateHotelList() {
		List<String> filterList = Arrays.asList(
				filterName.getValue().toLowerCase(),
				filterAddress.getValue().toLowerCase());

		dataProvider = new HotelDataProvider().withConfigurableFilter();   //<--without this line unable to edit 
		grid.setDataProvider(dataProvider);									//a hotel twice without browser page refresh
		dataProvider.setFilter(filterList);

		grid.asMultiSelect().clear();

	}

	private void manageButtons(boolean addButton, boolean editButton, boolean deleteButton, boolean bulkEnabled) {
		addHotel.setEnabled(addButton);
        saveHotel.setEnabled(editButton);
        deleteHotel.setEnabled(deleteButton);
        bulk.setEnabled(bulkEnabled);
    }


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) { 	//update info on change view
		if (!isViewCreated) {		//do not redraw components on each enter
			init();
			isViewCreated = true;
		}
	    updateHotelList();
	    hotelForm.updateAvailableCategories();
    }

}
