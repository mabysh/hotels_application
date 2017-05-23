package com.demo.app.hotel.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class HotelsUITest extends AbstractUITest {

	@Test
	public void deleteHotel() throws InterruptedException {
		driver.get(BASE_URL);
		Thread.sleep(2000);

		List<WebElement> elementList = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
		int listSize = elementList.size();

		Assert.assertTrue("NO HOTELS", listSize > 0);
		int random = (int) (Math.random() * listSize);
		//random hotel
		WebElement randomRow = elementList.get(random);
		//fetch its name
		String name = randomRow.findElement(By.xpath(".//td[4]")).getText();
		//select hotel
		WebElement checkBox = randomRow.findElement(By.xpath(".//td[1]"));
		checkBox.click();
		Thread.sleep(1000);

		//delete operation
		WebElement deleteButton = driver.findElement(By
						.xpath("//div[contains(@class, 'v-button v-widget delete_hotel')]"));
		deleteButton.click();
		Thread.sleep(1000);
		
		//now try to find it
		driver.findElement(By.xpath("//input[@placeholder='filter by name']")).sendKeys(name);
		Thread.sleep(1000);
		listSize = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]")).size();

		Assert.assertTrue("HOTEL STILL PRESENT", listSize == 0);
	}
	
	@Test
	public void createNewHotel() throws InterruptedException{
		driver.get(BASE_URL);
		Thread.sleep(1000);

		//press NewHotel button
		driver.findElement(By
				.xpath("//div[contains(@class, 'v-button v-widget add_hotel')]")).click();
		
		Thread.sleep(1000);
		//find hotel form
		WebElement hotelForm = driver.findElement(By
				.xpath("//div[@class='v-formlayout v-layout v-widget']/table/tbody"));
		//find name row
		WebElement nameRow = hotelForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Name')]"));
		//send something to name field
		nameRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/input")).sendKeys("New Name");
		Thread.sleep(1000);

		//find address row
WebElement addressRow = hotelForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Address')]"));
		//send something to address field
		addressRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/input")).sendKeys("New Address");
		Thread.sleep(1000);

		//press rating filterselect button
		WebElement ratingRow = hotelForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Rating')]"));
		ratingRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/div/div")).click();
		Thread.sleep(1000);
		//inspecting dropdowns is a hell! but i managed it
		List<WebElement> rating = driver.findElements(By
				.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']/div/div[2]/table/tbody/tr"));
		//select random option
		rating.get((int) ((Math.random() * (rating.size()-1)) + 1)).click();
		Thread.sleep(1000);

		//press category filterselect button
		WebElement categoryRow = hotelForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Category')]"));
		categoryRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/div/div")).click();
		Thread.sleep(1000);
		//this component appears after clicking
		List<WebElement> categories = driver.findElements(By
				.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']/div/div[2]/table/tbody/tr"));
		//select random option
		categories.get((int) ((Math.random() * (categories.size()-1)) + 1)).click();
		Thread.sleep(1000);

		//find url row
		WebElement urlRow = hotelForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Link')]"));
		//send something to name field
		urlRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/input")).sendKeys("booking.com");
		Thread.sleep(1000);
		
		//popup calendar
		driver.findElement(By.xpath("//button[@class='v-datefield-button']")).click();
		Thread.sleep(1000);
		//select previous month
		driver.findElement(By.xpath("//button[@class='v-button-prevmonth']")).click();
		Thread.sleep(1000);
		//select day
		List<WebElement> weeks = driver.findElements(By
				.xpath("//table[@id='PID_VAADIN_POPUPCAL']/tbody/tr[2]/td/table/tbody/tr"));
		WebElement week = weeks.get((int) (Math.random() * 5 + 2));
		List<WebElement> days = week.findElements(By.xpath(".//td"));
		days.get((int) (Math.random() * 7 + 1)).click();
		Thread.sleep(1000);
		
		//change payment method
		WebElement radio = driver.findElement(By
				.xpath("//span[@class='v-radiobutton v-select-option'][contains(./label/text(), 'Credit Card')]"));
		radio.click();
		Thread.sleep(1000);
		WebElement feeText = driver.findElement(By.xpath("//input[@placeholder='Prepay in percent...']"));
		feeText.clear();
		Thread.sleep(1000);
		feeText.sendKeys(String.valueOf((int)(Math.random() * 100) + 1));
		Thread.sleep(1000);
		feeText.clear();
		Thread.sleep(1000);
		feeText.sendKeys(String.valueOf((int)(Math.random() * 100) + 1));
		Thread.sleep(1000);
		
		//set description
		WebElement descriptionRow = hotelForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Description')]"));
		//send something to textarea
		descriptionRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/textarea")).sendKeys("Awesome hotel.");
		Thread.sleep(1000);
		
		//finally, save
		WebElement saveButton = driver.findElement(By
						.xpath("//div[contains(@class, 'v-button v-widget save_hotel')]"));
		saveButton.click();
		Thread.sleep(1000);
		
		//now try to find it
		driver.findElement(By.xpath("//input[@placeholder='filter by name']")).sendKeys("New Name");
		Thread.sleep(1000);
		int listSize = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]")).size();

		Assert.assertTrue("HOTEL NOT FOUND", listSize > 0);
		Thread.sleep(3000);
	}
	
	@Test
	public void bulkUpdateTest() throws InterruptedException{
		final String updateString = "Bulk Update address";
		
		driver.get(BASE_URL);
		Thread.sleep(1000);
		List<WebElement> elementList = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
		for (int i = 0; i < elementList.size() - 2; i++) {
				if ((int) (Math.random() * 2) == 1) {
					elementList.get(i).findElement(By.xpath(".//td[1]")).click();
					Thread.sleep(500);
				}
		}
		Thread.sleep(1000);
		driver.findElement(By
				.xpath("//span[text()='Bulk Update']")).click();
		Thread.sleep(1000);

		//click combobox button
		driver.findElement(By
				.xpath("//div[@class='popupContent']/div/table/tbody/tr/td[3]/div/div[3]/div/div[2]/div")).click();
		Thread.sleep(1000);
		WebElement addressOption = driver.findElement(By
				.xpath("//div[@id='VAADIN_COMBOBOX_OPTIONLIST']/div/div[2]/table/tbody/tr[3]"));
		addressOption.click();
		Thread.sleep(1000);

		//send new address to the field
		driver.findElement(By
				.xpath("//div[@class='popupContent']/div/table/tbody/tr/td[3]/div/div[5]/div[1]/input")).sendKeys(updateString);
		Thread.sleep(1000);

		//save changes
		driver.findElement(By
				.xpath("//div[@class='popupContent']/div/table/tbody/tr/td[3]/div/div[7]/div/div[1]/div")).click();
		Thread.sleep(1000);

		//now try to find it
		driver.findElement(By.xpath("//input[@placeholder='filter by address']")).sendKeys(updateString);
		Thread.sleep(1000);
		int listSize = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]")).size();

		Assert.assertTrue("HOTEL NOT FOUND", listSize > 0);
		Thread.sleep(3000);
	}
}
