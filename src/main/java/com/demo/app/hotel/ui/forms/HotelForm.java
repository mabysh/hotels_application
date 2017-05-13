package com.demo.app.hotel.ui.forms;

import com.demo.app.hotel.backend.*;
import com.demo.app.hotel.ui.representation.DateConverter;
import com.demo.app.hotel.ui.representation.StarsConverter;
import com.demo.app.hotel.ui.views.HotelsView;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.List;

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

	private ApplicationService service = ApplicationService.getInstance();
	private List<Category> categories = (List<Category>) service.findAll(Category.class);
	private Hotel hotel;
	private HotelsView view;
	private Binder<Hotel> binder = new Binder<>(Hotel.class);
	private final String[] RATING_ITEMS = {"\u2605",
											"\u2605\u2605",
											"\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605\u2605"};

	public HotelForm(HotelsView view) {
		this.view = view;
		
		setSizeUndefined();
		HorizontalLayout buttons = new HorizontalLayout(save, delete);
		addComponents(name, address, rating, category, url, operatesFrom, description, buttons);

		rating.setItems(RATING_ITEMS);
		category.setItems(categories);		//initial set. see updateAvailableCategories() method;
		category.setItemCaptionGenerator(Category::getName);
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

	public void setHotel(Hotel h) {
		this.hotel = h;
		binder.readBean(h);
		delete.setVisible(h.isPersisted());
		setVisible(true);
		name.selectAll();
	}
	
	private void delete() {
		service.delete(hotel);
		view.updateHotelList();
		setVisible(false);
		Notification.show("Hotel has been deleted");
	}

	private void save() {
		try {
			binder.writeBean(hotel);
			service.save(hotel);
		} catch (ValidationException e ) {
			Notification.show("Hotel could not be saved, " +
        "please check data for each field.");
			return;												//do not hide the form on invalid input
		}
		view.updateHotelList();
		//ui.updateCategoryList();
		setVisible(false);
		Notification.show("Hotels has been updated");
	}

	public void updateAvailableCategories() {                   //this method aimed to update list of available
        this.categories = (List<Category>) service.findAll(Category.class);
        category.setItems(categories);
    }
}
