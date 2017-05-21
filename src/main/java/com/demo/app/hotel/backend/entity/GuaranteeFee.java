package com.demo.app.hotel.backend.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@SuppressWarnings("serial")
@Embeddable
public class GuaranteeFee implements Serializable {
	
	public enum PaymentMethod {
		CASH, CARD
	}

	@Column(name = "GUARANTEE_FEE")
	private Integer feeValue = null;
	
	@Transient
	private Integer oldValue = null;
	
	@Transient
	private PaymentMethod method = null;
	
	public PaymentMethod getPaymentMethod() {
		return method;
	}
	
	public void setPaymentMethod(PaymentMethod method) {
		this.method = method;
	}
	
	public Integer getFeeValue() {
		if (feeValue == null)
			return 0;
		return feeValue;
	}

	public void setFeeValue(Integer feeValue) {
		this.oldValue = this.feeValue;
		this.feeValue = feeValue;
	}
	
	public Integer getOldValue() {
		if (oldValue == null)
			return 0;
		return oldValue;
	}
	
	public void setOldValue(Integer value) {
		this.oldValue = value;
	}
	
	public boolean isValid() {
		if ((method == PaymentMethod.CASH && getFeeValue() == 0) ||
			(method == PaymentMethod.CARD && getFeeValue() > 0 && getFeeValue() <= 100))
			return true;
		return false;
	}

	public GuaranteeFee() {
		super();
		this.feeValue = null;
		this.oldValue = null;
		this.method = PaymentMethod.CASH;
	}
	
	public GuaranteeFee(GuaranteeFee fee) {
		super();
		this.feeValue = fee.feeValue;
		this.oldValue = fee.oldValue;
		this.method = fee.method;
	}

	@Override
	public String toString() {
		return "Fee Value " + feeValue + "; Old Value " + oldValue + "; method " + method;
	}


}
