package com.demo.app.hotel.ui.views;

import com.demo.app.hotel.backend.ApplicationService;
import com.demo.app.hotel.backend.Hotel;
import com.demo.app.hotel.ui.forms.HotelForm;
import com.demo.app.hotel.ui.representation.CategoryRenderer;
import com.demo.app.hotel.ui.representation.OperatesFromRenderer;
import com.demo.app.hotel.ui.representation.StarsRenderer;
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
import java.util.List;
import java.util.Set;

@SpringComponent
@UIScope
@SpringView(name = HotelsView.VIEW_NAME)
public class HotelsView extends VerticalLayout implements View {

    public static final String VIEW_NAME = "hotels";

	private ApplicationService service = ApplicationService.getInstance();

	private Button addHotel;
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

	@PostConstruct
    void init() {
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

		addHotel = new Button("Add new Hotel");
		addHotel.addClickListener(e -> {
			grid.asSingleSelect().clear();
			hotelForm.setHotel(new Hotel());
		});

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
		clearFilter.addClickListener(e -> {
			filterAddress.clear();
			filterName.clear();
		});

		hotelForm.setVisible(false);
	}

	private void setUpHotelGrid() {

    	//Configure hotel list grid
		grid.setColumns("rating", "category", "name", "address", "operatesFrom");
		grid.getColumn("category").setRenderer(new CategoryRenderer()).setCaption("Category");
		grid.getColumn("rating").setRenderer(new StarsRenderer());
		grid.getColumn("operatesFrom").setRenderer(new OperatesFromRenderer()).setCaption("Operates for");
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
        		Hotel h = event.getValue();
        		hotelForm.setHotel(h);
        	}
        });

	}

	private void setUpHotelLayouts() {

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
	}

	public void updateHotelList() {
		String addrFilter = filterAddress.getValue().toLowerCase();
		String nameFilter = filterName.getValue().toLowerCase();

		List<Hotel> hotelList = (List<Hotel>) service.findAll(nameFilter, addrFilter, Hotel.class);

        grid.setItems(hotelList);
	}


    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
	    updateHotelList();
	    hotelForm.updateAvailableCategories();
    }

}
