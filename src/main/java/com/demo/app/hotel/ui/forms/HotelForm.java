package com.demo.app.hotel.ui.forms;

import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.Hotel;
import com.demo.app.hotel.backend.service.ApplicationServiceImpl;
import com.demo.app.hotel.ui.representation.DateConverter;
import com.demo.app.hotel.ui.representation.StarsConverter;
import com.demo.app.hotel.ui.views.HotelsView;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.*;

import java.util.List;
import java.util.Set;

public class HotelForm extends FormLayout {

	protected TextField name = new TextField("Name");
	protected TextField address = new TextField("Address");
	protected ComboBox<String> rating = new ComboBox<>("Rating");
	protected ComboBox<Category> category = new ComboBox<>("Category");
	protected TextField url = new TextField("Link");
	protected DateField operatesFrom = new DateField("Operates From");
	protected TextArea description = new TextArea("Description");

	protected ApplicationServiceImpl service = ApplicationServiceImpl.getInstance();
	protected List<Category> categories = service.findAllCategories();
	protected Set<Hotel> hotels;
	protected HotelsView view;
	protected Binder<Hotel> binder = new Binder<>(Hotel.class);
	protected final String[] RATING_ITEMS = {"\u2605",
											"\u2605\u2605",
											"\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605\u2605"};

	public HotelForm(HotelsView view) {
		this.view = view;
		
		setSizeUndefined();
		addComponents(name, address, rating, category, url, operatesFrom, description);

		rating.setItems(RATING_ITEMS);
		rating.setPlaceholder("Select rating");
		category.setItems(categories);		//initial set. see updateAvailableCategories() method;
		category.setItemCaptionGenerator(Category::getName);	//getName() instead of toString() is the source;
		category.setPlaceholder("Please, select category");

		name.setRequiredIndicatorVisible(true);
		address.setRequiredIndicatorVisible(true);
		category.setRequiredIndicatorVisible(true);
		rating.setRequiredIndicatorVisible(true);
		url.setRequiredIndicatorVisible(true);
		operatesFrom.setRequiredIndicatorVisible(true);

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
				.withValidator(new RegexpValidator("Incorrect name!", "[0-9a-zA-Z,.& /\\-]*+"))
				.bind(Hotel::getName, Hotel::setName);
		binder.forField(address)
				.asRequired("Enter an address to proceed.")
				.bind(Hotel::getAddress, Hotel::setAddress);
		binder.forField(category)
				.asRequired("Please, choose category")
				.withValidator(val -> (val != null), "Specify category")
				.bind(Hotel::getCategory, Hotel::setCategory);
		binder.forField(url)
				.asRequired("Enter an url to Booking.com.")
				.withValidator(new RegexpValidator("Incorrect url!",
						"^(https://www.booking.com).*|^(www.booking.com).*|^(booking.com).*"))
				.bind(Hotel::getUrl, Hotel::setUrl);
		binder.forField(description).bind(Hotel::getDescription, Hotel::setDescription);
		binder.forField(operatesFrom).withConverter(new DateConverter())
				.asRequired("Please, choose date")
				.withValidator(val -> (val >= 0), "Wrong date! Can't be future")
				.bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);
	}

	public void setHotels(Set<Hotel> hotels) {
		this.hotels = hotels;
		for (Hotel hotel : hotels) {
			binder.readBean(hotel);
		}
		setVisible(true);
		name.selectAll();
	}

	public void delete() {
	    for (Hotel hotel : hotels) {
	    	binder.readBean(hotel);
	    	service.delete(hotel);
		}
		view.updateHotelList();
		setVisible(false);
		Notification.show("Hotel has been deleted");
	}

	public void save() {
		try {
			for (Hotel hotel : hotels) {
				binder.writeBean(hotel);
	    		service.save(hotel);
		}
		} catch (ValidationException e ) {
			Notification.show("Hotel could not be saved, " +
        "please check data for each field.");
			return;												//do not hide the form on invalid input
		}
		view.updateHotelList();
		setVisible(false);
		Notification.show("Hotels has been updated");
	}

	public void updateAvailableCategories() {                   //this method aimed to update list of available
        this.categories = service.findAllCategories();
        category.setItems(categories);
    }
}
