package com.demo.app.hotel;

import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Set;

public class HotelForm extends FormLayout {

	private TextField name = new TextField("Name");
	private TextField address = new TextField("Address");
	private NativeSelect<String> rating = new NativeSelect<>("Rating");
	private NativeSelect<Category> category = new NativeSelect<>("Category");
	private TextField url = new TextField("Link");
	private DateField operatesFrom = new DateField("Operates From");
	private TextArea description = new TextArea("Description");
	private Button save = new Button("Save");
	private Button delete = new Button("Delete");
	
	private HotelService hotelService = HotelService.getInstance();
	private CategoryService categoryService = CategoryService.getInstance();
	private Set<Category> categories = categoryService.findAllCategories();
	private Hotel hotel;
	private HotelsUI ui;
	private Binder<Hotel> binder = new Binder<>(Hotel.class);
	private final String[] RATING_ITEMS = {"\u2605",
											"\u2605\u2605",
											"\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605\u2605"};

	public HotelForm(HotelsUI ui) {
		this.ui = ui;
		
		setSizeUndefined();
		HorizontalLayout buttons = new HorizontalLayout(save, delete);
		addComponents(name, address, rating, category, url, operatesFrom, description, buttons);

		rating.setItems(RATING_ITEMS);
		category.setItems(categories);		//initial set. see updateAvailableCategories() method;
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(KeyCode.ENTER);

		name.setRequiredIndicatorVisible(true);
		address.setRequiredIndicatorVisible(true);
		category.setRequiredIndicatorVisible(true);
		rating.setRequiredIndicatorVisible(true);
		url.setRequiredIndicatorVisible(true);
		operatesFrom.setRequiredIndicatorVisible(true);

		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());

		bindFields();
		setUpTooltips();
	}

	private void setUpTooltips() {
	    name.setDescription("Institution Name");
	    address.setDescription("Institution Location");
	    rating.setDescription("Institution rating");
		category.setDescription("Category of an apartment");
		url.setDescription("Link to Booking.com website");
		operatesFrom.setDescription("Opened since...");
		description.setDescription("Institution description");
	}

	private void bindFields() {
	    binder.forField(rating).withConverter(new StarsConverter())
                .asRequired("Please, choose rating")
				.bind(Hotel::getRating, Hotel::setRating);
	    binder.forField(name)
				.asRequired("Enter a name to proceed.")
				.bind(Hotel::getName, Hotel::setName);
		binder.forField(address)
				.asRequired("Enter an address to proceed.")
				.bind(Hotel::getAddress, Hotel::setAddress);
		binder.forField(category)
				.asRequired("Please, choose category")
				.bind(Hotel::getCategory, Hotel::setCategory);
		binder.forField(url)
				.asRequired("Enter an url to Booking.com.")
				.bind(Hotel::getUrl, Hotel::setUrl);
		binder.forField(description).bind(Hotel::getDescription, Hotel::setDescription);
		binder.forField(operatesFrom).withConverter(new DateConverter())
				.asRequired("Please, choose date")
				.withValidator(val -> (val >= 0), "Wrong date! Can't be in a future")
				.bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		binder.readBean(hotel);
		
		delete.setVisible(hotel.isPersisted());
		setVisible(true);
		name.selectAll();
	}
	
	private void delete() {
		hotelService.delete(hotel);
		ui.updateHotelList();
		ui.updateCategoryList();
		setVisible(false);
		Notification.show("Hotel has been deleted");
	}
	
	private void save() {
		try {
			binder.writeBean(hotel);
			hotelService.save(hotel);
		} catch (ValidationException e ) {
			Notification.show("Hotel could not be saved, " +
        "please check error messages for each field.");
		}
		ui.updateHotelList();
		ui.updateCategoryList();
		setVisible(false);
		Notification.show("Hotels has been updated");
	}

	public void updateAvailableCategories() {                   //this method aimed to update list of available
	    this.categories = categoryService.findAllCategories();  //categories when some of them gets changed
        category.setItems(categories);
    }
}
