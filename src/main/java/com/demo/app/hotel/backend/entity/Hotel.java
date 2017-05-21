package com.demo.app.hotel.backend.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Entity
@NamedQueries({
		@NamedQuery(name = "Hotel.filter",
				query = "SELECT e FROM Hotel e WHERE LOWER(e.name) LIKE :nameFilter" +
						" AND LOWER(e.address) LIKE :addressFilter")
})
@Table(name = "HOTEL")

public class Hotel extends AbstractEntity {

	@NotNull
	@Pattern(message = "Incorrect name!", regexp = "[0-9a-zA-Z ]*+")
	private String name;
	@NotNull
	@Pattern(message = "Incorrect address!", regexp = "[0-9a-zA-Z,. /\\-]*+")
	private String address;
	@NotNull
	private Integer rating;

	@NotNull
	@Column(name = "OPERATES_FROM")
	private Long operatesFrom;

	@ManyToOne
	@JoinColumn(name = "CATEGORY_ID", foreignKey = @ForeignKey(name = "fk_hotel_category"))
	private Category category;
	@NotNull
	private String url;
	@Pattern(message = "Incorrect url!", regexp = "^(https://www.booking.com).*|^(www.booking.com).*|^(booking.com).*")
	private String description;
	
	@Embedded
	@NotNull
	private GuaranteeFee guaranteeFee;

	@Version
	@Column(name = "OPTLOCK")
	private int version;

	public GuaranteeFee getGuaranteeFee() {
		return guaranteeFee;
	}
	
	public void setGuaranteeFee(GuaranteeFee fee) {
		this.guaranteeFee = fee;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Integer getRating() {
			return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Long getOperatesFrom() {
		return operatesFrom;
	}

	public void setOperatesFrom(Long operatesFrom) {
		this.operatesFrom = operatesFrom;
	}

	public Category getCategory() {
		return category;
	}

	public String getCategoryName() { return category.getName(); }

	public void setCategory(Category category) {
		this.category = category;
	}	

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVersion() { return version; }

	@Override
	public String toString() {
		return name + " " + rating +"stars " + address;
	}

	@Override
	protected Hotel clone() throws CloneNotSupportedException {
		return (Hotel) super.clone();
	}

	public Hotel() {
	    this.name = "";
	    this.address = "";
	    this.description = "";
	}

	public Hotel(String name, String address, Integer rating, Long operatesFrom, Category category, String url, GuaranteeFee fee) {
		super();
		this.name = name;
		this.address = address;
		this.rating = rating;
		this.operatesFrom = operatesFrom;
		this.category = category;
		this.url = url;
		this.guaranteeFee = fee;
	}

}
