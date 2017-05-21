package com.demo.app.hotel.ui.forms;

import java.util.Arrays;

import com.demo.app.hotel.backend.entity.GuaranteeFee;
import com.demo.app.hotel.backend.entity.GuaranteeFee.PaymentMethod;
import com.vaadin.data.Binder;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.RadioButtonGroup;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;


@SuppressWarnings("serial")
public class GuaranteeFeeField extends CustomField<GuaranteeFee> {
	
	private GuaranteeFee value = new GuaranteeFee();
	private RadioButtonGroup<String> radio;
	private TextField field;
	private Label label = new Label("Payment will be made \ndirectly in hotel",
			ContentMode.PREFORMATTED);
	
	public GuaranteeFeeField(String caption) {
		setCaption(caption);
	}

	@Override
	public GuaranteeFee getValue() {
		return value;
	}

	@Override
	protected Component initContent() {
		VerticalLayout layout = new VerticalLayout();
		layout.setMargin(false);
		radio = new RadioButtonGroup<>();
		radio.setItems(Arrays.asList("Cash", "Credit Card"));
		radio.addStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL); 
		radio.addValueChangeListener(e -> {
			if (e.getValue().equals("Cash")) {
				value.setPaymentMethod(PaymentMethod.CASH);		
				value.setFeeValue(0);						//moves feeValue to oldFeeValue so we may get it later (see method)
				field.setVisible(false);
				label.setVisible(true);
				this.setValue(new GuaranteeFee(value));		//fire event
			} else {
				value.setPaymentMethod(PaymentMethod.CARD);
				if (value.getFeeValue() == 0 || value.getFeeValue() == -1) {				//recover value when switching from cash method	
					value.setFeeValue(value.getOldValue() == 0 || value.getOldValue() == -1 ? 
										1 : value.getOldValue());								
					value.setOldValue(null);
				}
				if (field.getValue().equals(String.valueOf(value.getFeeValue()))) {	//(too) smart notification logic. need to fire event by hand to see
					this.setValue(new GuaranteeFee(value));
				} else {
					field.setValue(String.valueOf(value.getFeeValue()));
				}
				field.setVisible(true);
				label.setVisible(false);
			}
		});
		
		field = new TextField();
		field.setMaxLength(3);
		field.setPlaceholder("Prepay in percent...");
		field.addValueChangeListener(e -> {
			validateField(e.getValue());
		});
		layout.addComponents(radio, field, label);
		return layout;
	}
	@Override
	protected void doSetValue(GuaranteeFee value) {
		this.value = value == null ? new GuaranteeFee() : value;
		radio.setValue(this.value.getFeeValue() == 0 ? "Cash" : "Credit Card");		//required for initial set
	}

	private void validateField(String text) {
		try {
			field.setComponentError(null);
			int fee = Integer.parseInt(text);
			if (fee > 0 && fee <= 100) {
				this.value.setFeeValue(fee);
				this.setValue(new GuaranteeFee(value));
				return;
			}
			field.setComponentError(new UserError("Must be from 1 to 100"));
			this.value.setFeeValue(-1);
			this.setValue(new GuaranteeFee(value));
		} catch (NumberFormatException ex) {
			field.setComponentError(new UserError("Must be digits!"));
			this.value.setFeeValue(-1);
			this.setValue(new GuaranteeFee(value));
		}
	}

}
