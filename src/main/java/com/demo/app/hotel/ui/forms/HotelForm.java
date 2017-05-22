package com.demo.app.hotel.ui.forms;

import com.demo.app.hotel.backend.entity.Category;
import com.demo.app.hotel.backend.entity.GuaranteeFee;
import com.demo.app.hotel.backend.entity.Hotel;
import com.demo.app.hotel.backend.entity.GuaranteeFee.PaymentMethod;
import com.demo.app.hotel.backend.service.ServiceFactory;
import com.demo.app.hotel.ui.representation.DateConverter;
import com.demo.app.hotel.ui.representation.StarsConverter;
import com.demo.app.hotel.ui.views.HotelsView;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;

import java.util.List;
import java.util.Set;

@SuppressWarnings("serial")
public class HotelForm extends FormLayout {

	protected TextField name = new TextField("Name");
	protected TextField address = new TextField("Address");
	protected ComboBox<String> rating = new ComboBox<>("Rating");
	protected ComboBox<Category> category = new ComboBox<>("Category");
	protected TextField url = new TextField("Link");
	protected DateField operatesFrom = new DateField("Operates From");
	protected TextArea description = new TextArea("Description");
	protected GuaranteeFeeField guaranteeFee = new GuaranteeFeeField("Payment method");

	protected List<Category> categories;
	protected Set<Hotel> hotels;
	protected HotelsView view;
	protected Binder<Hotel> binder = new Binder<>(Hotel.class);
	protected final String[] RATING_ITEMS = {"\u2605",
											"\u2605\u2605",
											"\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605",
											"\u2605\u2605\u2605\u2605\u2605"};
	protected PaymentMethod lastMethod;
	protected boolean showFeeNotification;

	public HotelForm(HotelsView view) {
		this.view = view;
		categories = ServiceFactory.getApplicationServiceImpl().findAllCategories();
		setUpForms();
		bindFields();
	}

	private void setUpForms() {
		setSizeUndefined();
		addComponents(name, address, rating, category, url, operatesFrom, guaranteeFee, description);

		rating.setItems(RATING_ITEMS);
		rating.setPlaceholder("Select rating");
		category.setItems(categories);							//initial set. see updateAvailableCategories() method;
		category.setItemCaptionGenerator(Category::getName);	//getName() instead of toString() is the source;
		category.setPlaceholder("Please, select category");

		name.setRequiredIndicatorVisible(true);
		address.setRequiredIndicatorVisible(true);
		category.setRequiredIndicatorVisible(true);
		rating.setRequiredIndicatorVisible(true);
		url.setRequiredIndicatorVisible(true);
		operatesFrom.setRequiredIndicatorVisible(true);
		guaranteeFee.setRequiredIndicatorVisible(true);

		name.setDescription("Institution Name");
	    address.setDescription("Institution Location");
	    rating.setDescription("Institution rating");
		category.setDescription("Category of an apartment");
		url.setDescription("Link to Booking.com website");
		operatesFrom.setDescription("Opened since...");
		description.setDescription("Institution description");
		guaranteeFee.setDescription("Payment method");
		
		guaranteeFee.addValueChangeListener(e -> {
			notificate(e.getValue());
			binder.validate();
		});
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
		binder.forField(guaranteeFee)
				.asRequired("Please, choose payment method")
				.withValidator(val -> (val.isValid()),	"Not valid")
				.bind(Hotel::getGuaranteeFee, Hotel::setGuaranteeFee);
	}

	public void setHotels(Set<Hotel> hotels) {
		showFeeNotification = false;
		this.hotels = hotels;
		for (Hotel hotel : hotels) {
			binder.readBean(hotel);
		}
		setVisible(true);
		name.selectAll();
		if (hotels.size() == 1) {
			showFeeNotification = true; 	//show fee notification on edit only
		}
	}

	public void delete() {
	    for (Hotel hotel : hotels) {
	    	binder.readBean(hotel);
	    	ServiceFactory.getApplicationServiceImpl().delete(hotel);
		}
		view.updateHotelList();
		setVisible(false);
		Notification.show("Hotel has been deleted");
	}

	public void save() {
		if (binder.hasChanges()) {
			try {
				for (Hotel hotel : hotels) {
					binder.writeBean(hotel);
					ServiceFactory.getApplicationServiceImpl().save(hotel);
				}
			} catch (ValidationException e ) {
				Notification.show("Hotel could not be saved, " +
						"please check data for each field.");
				return;												//do not hide the form on invalid input
			}
			view.updateHotelList();
			setVisible(false);
			Notification.show("Hotels have been updated");
		} else {
			Notification.show("No changes have been made");
		}
	}
	
	public void notificate(GuaranteeFee feeObj) { 		//some smart notification
		if (showFeeNotification) {
			Integer fee = feeObj.getFeeValue();
			Integer oldFee = feeObj.getOldValue();
			PaymentMethod method = feeObj.getPaymentMethod();
			StringBuilder str = new StringBuilder("");
			if (lastMethod == null || method == PaymentMethod.CASH || method != lastMethod) {
				str.append("Payment method: " + method + "<br>");
				lastMethod = method;
			}
			if (method == PaymentMethod.CARD) {
				str.append("Value changed");
				if (oldFee != 0 && oldFee != -1 && fee != oldFee) {
					str.append(" from ").append(oldFee);
				}
				str.append(" to ").append(fee).append("%.");
			}
			if (!str.toString().isEmpty() && binder.isValid()) {
				Notification n = new Notification(str.toString(), null, Type.TRAY_NOTIFICATION, true);
				n.show(getUI().getPage());
			} 
		}
	}

	public void updateAvailableCategories() {                   //this method aimed to update list of available categories
        this.categories = ServiceFactory.getApplicationServiceImpl().findAllCategories();
        category.setItems(categories);
    }
}
