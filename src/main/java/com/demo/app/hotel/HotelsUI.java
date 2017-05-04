package com.demo.app.hotel;

import java.text.Normalizer.Form;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

@Theme("mytheme")
public class HotelsUI extends UI {
	
	private HotelService Service = HotelService.getInstance();
	private Grid<Hotel> grid = new Grid<>(Hotel.class);
	private VerticalLayout root;
    private HorizontalLayout gridLayout, toolbar;
    private Label description = new Label("");
    private TextField filterAddress, filterName;
    private Button clearFilter, addHotel;
    private HotelForm form = new HotelForm(this);

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpForms();
    	setUpGrid();
    	setUpLayout();
    }

	private void setUpLayout() {
		root = new VerticalLayout();
		gridLayout = new HorizontalLayout();
        gridLayout.setSizeFull();
        gridLayout.setHeight("500");
        gridLayout.addComponents(grid, form);
        gridLayout.setExpandRatio(grid, 1);
	    CssLayout filterLayout = new CssLayout();
		filterLayout.addComponents(filterName, filterAddress, clearFilter);
		filterLayout.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		
		toolbar = new HorizontalLayout(filterLayout, addHotel);

		root.addComponents(toolbar, gridLayout, description);
        setContent(root);

	}

	private void setUpGrid() {
		grid.setColumns("rating", "category", "name", "address", "operatesFrom");
        grid.addColumn(hotel -> "<a href='" + hotel.getUrl() + "' target='_top'>View at Booking.com</a>",
        		new HtmlRenderer()).setCaption("Link to Booking.com");
        grid.setSizeFull();
        updateList();
        grid.addSelectionListener(e -> {
    		Set<Hotel> selected = e.getAllSelectedItems();
    		if (!selected.isEmpty()) {
    			Hotel sel = (Hotel) selected.toArray()[0];
    			description.setValue(sel.getDescription());
    		}
       	});
        grid.asSingleSelect().addValueChangeListener(event -> {
        	if (event.getValue() == null) {
        		form.setVisible(false);
        	} else {
        		form.setHotel(event.getValue());
        	}
        });
	}

	private void setUpForms() {
		addHotel = new Button("Add new Hotel");
		addHotel.addClickListener(e -> {
			grid.asSingleSelect().clear();
			form.setHotel(new Hotel());
		});
		form.setVisible(false);
		
		filterAddress = new TextField();
		filterAddress.setPlaceholder("filter by address");
		filterAddress.addValueChangeListener(e -> updateList());
		filterAddress.setValueChangeMode(ValueChangeMode.LAZY);;
		filterName = new TextField();
		filterName.setPlaceholder("filter by name");
		filterName.addValueChangeListener(e -> updateList());
		filterName.setValueChangeMode(ValueChangeMode.LAZY);;
		
		clearFilter = new Button(VaadinIcons.CLOSE);
		clearFilter.setDescription("Clear the current filter");
		clearFilter.addClickListener(e -> {
			filterAddress.clear();
			filterName.clear();
		});
	}

	public void updateList() {
		String addrFilter = filterAddress.getValue();
		String nameFilter = filterName.getValue();
			
		List<Hotel> hotelList = Service.findAll(nameFilter, addrFilter);
        grid.setItems(hotelList);
	}

    @WebServlet(urlPatterns = "/*", name = "HotelsUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = HotelsUI.class, productionMode = false)
    public static class HotelsUIServlet extends VaadinServlet {
    }
}
