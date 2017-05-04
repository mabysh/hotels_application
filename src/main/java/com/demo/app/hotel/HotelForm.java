package com.demo.app.hotel;

import java.util.stream.Stream;

import com.vaadin.data.Binder;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

public class HotelForm extends FormLayout {
	
	private final String STAR = "\u2605";

	private TextField name = new TextField("Name");
	private TextField address = new TextField("Address");
	private NativeSelect<HotelCategory> category = new NativeSelect<>("Category");
	private NativeSelect<String> rating = new NativeSelect<>("Rating");
	private TextField url = new TextField("Link");
	private DateField operatesFrom = new DateField("Operates From");
	private TextArea description = new TextArea("Description");
	private Button save = new Button("Save");
	private Button delete = new Button("Delete");
	
	private HotelService service = HotelService.getInstance();
	private Hotel hotel;
	private HotelsUI ui;
	private Binder<Hotel> binder = new Binder<>(Hotel.class);
	
	public HotelForm(HotelsUI ui) {
		this.ui = ui;
		
		setSizeUndefined();
		HorizontalLayout buttons = new HorizontalLayout(save, delete);
		addComponents(name, address, category, rating, url, operatesFrom, description, buttons);
		
		category.setItems(HotelCategory.values());
		rating.setItems(new String[] {STAR, STAR+STAR, STAR+STAR+STAR, STAR+STAR+STAR+STAR});
		save.setStyleName(ValoTheme.BUTTON_PRIMARY);
		save.setClickShortcut(KeyCode.ENTER);
		
		binder.bindInstanceFields(this);
		
		save.addClickListener(e -> save());
		delete.addClickListener(e -> delete());
	}
	
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
		binder.setBean(hotel);
		
		delete.setVisible(hotel.isPersisted());
		setVisible(true);
		name.selectAll();
	}
	
	private void delete() {
		service.delete(hotel);
		ui.updateList();
		setVisible(false);
	}
	
	private void save() {
		service.save(hotel);
		ui.updateList();
		setVisible(false);
	}
}
