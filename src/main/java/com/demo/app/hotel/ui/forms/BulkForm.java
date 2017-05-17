package com.demo.app.hotel.ui.forms;


import com.demo.app.hotel.backend.entity.Hotel;
import com.demo.app.hotel.ui.representation.DateConverter;
import com.demo.app.hotel.ui.representation.StarsConverter;
import com.demo.app.hotel.ui.views.HotelsView;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
 * This class is just another representation of HotelForm
 */

public class BulkForm extends HotelForm {

    private VerticalLayout layout = new VerticalLayout();
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button save, close;
    private ComboBox<Field> comboBox = new ComboBox<>("Field to update");
    private Label bulkLabel = new Label("Bulk Update");
    private List<Field> formFields;
    private Binder<Hotel> newBinder = new Binder<>(Hotel.class);

    public BulkForm(HotelsView view) {
        super(view);
        setUpForms();
    }


    @Override
    public void setHotels(Set<Hotel> hotels) {
        this.hotels = hotels;
        manageFields(null);
    }

    @Override
    public void save() {
        try {
            for (Hotel hotel : hotels) {
                newBinder.writeBean(hotel);
                service.save(hotel);
            }
        } catch (ValidationException e) {
            Notification.show("Hotel could not be saved, " +
                    "please check data for each field.");
            return;                                                //do not hide the form on invalid input
        }
        view.updateHotelList();
        view.hidePopup();
        hotels = null;
        Notification.show("Hotels has been updated");
    }

    private void setUpForms() {
        removeAllComponents();
        layout.addComponents(bulkLabel, comboBox, name, address, rating, category, url,
                operatesFrom, description, buttons);
        addComponent(layout);

        bulkLabel.addStyleName("h1");
        save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(e -> save());
        close = new Button("Close");
        close.addClickListener(e -> view.hidePopup());
        buttons.addComponents(save, close);

        //get all fields of the Hotel Form
        List<Field> fields = Arrays.asList(HotelForm.class.getDeclaredFields());
        //here we filter those fields so only Component inheritors left (HotelsView excluded)
        formFields = fields.stream().filter(field ->
                AbstractComponent.class.isAssignableFrom(field.getType()) && !field.getName().equals("view"))
                .collect(Collectors.toList());
        //setting items to combo box
        comboBox.setItems(formFields);
        comboBox.setItemCaptionGenerator(Field::getName);
        comboBox.setPlaceholder("Please, select field");
        comboBox.addSelectionListener(event -> manageFields(event.getValue()));
        manageFields(null);
    }

    private void manageFields(Field selected) {
        comboBox.setSelectedItem(null);
        for (Field field : formFields) {
            try {
                AbstractComponent component = (AbstractComponent) field.get(this);  //getting component fields
                component.setVisible(false);                        //with some reflection api magic and hiding them
                if (selected != null && selected.getName().equals(field.getName())) {
                    component.setVisible(true);                     //all except selected one
                    bindFieldByName(selected.getName());            //see below
                }
            } catch (IllegalAccessException e) {
                Logger.getLogger(BulkForm.class.getName())
                        .log(Level.SEVERE, "No access to the field");
            }
        }
    }

    /*
     * For each field selection, new binder gets created with appropriate single field binded.
     * I know, this is a bunch of boilerplate code, but going this way allows us to use same HotelForm
     * components. I'd like to know any other approach to modify existing binder instead of create
     * new one ever time. Didn't find any usefull information for this matter.
     */
    private void bindFieldByName(String name) {
        newBinder = new Binder<>(Hotel.class);
        switch(name) {
            case "name": {
                newBinder.forField(this.name)
				.asRequired("Enter a name to proceed.")
				.withValidator(new RegexpValidator("Incorrect name!", "[0-9a-zA-Z,.& /\\-]*+"))
				.bind(Hotel::getName, Hotel::setName);
                break;
            }case "address": {
                newBinder.forField(address)
				.asRequired("Enter an address to proceed.")
				.bind(Hotel::getAddress, Hotel::setAddress);
                break;
            }case "category": {
                newBinder.forField(category)
				.asRequired("Please, choose category")
				.withValidator(val -> (val != null), "Specify category")
				.bind(Hotel::getCategory, Hotel::setCategory);
                break;
            }case "rating": {
                newBinder.forField(rating).withConverter(new StarsConverter())
                .asRequired("Please, choose rating")
				.bind(Hotel::getRating, Hotel::setRating);
                break;
            }case "url": {
                newBinder.forField(url)
				.asRequired("Enter an url to Booking.com.")
				.withValidator(new RegexpValidator("Incorrect url!",
						"^(https://www.booking.com).*|^(www.booking.com).*|^(booking.com).*"))
				.bind(Hotel::getUrl, Hotel::setUrl);
                break;
            }case "operatesFrom": {
                newBinder.forField(operatesFrom).withConverter(new DateConverter())
				.asRequired("Please, choose date")
				.withValidator(val -> (val >= 0), "Wrong date! Can't be future")
				.bind(Hotel::getOperatesFrom, Hotel::setOperatesFrom);
                break;
            }case "description": {
		        newBinder.forField(description).bind(Hotel::getDescription, Hotel::setDescription);
		        break;
            }
        }
    }
}
