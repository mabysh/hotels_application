package com.demo.app.hotel.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CategoryUITest extends AbstractUITest {
	
	@Test
	public void deleteCatogory() throws InterruptedException {
		driver.get(BASE_URL);
		Thread.sleep(2000);
		
		WebElement categoriesButton = driver.findElement(By
				.xpath("//span[@class='v-button-caption'][text()='Categories']"));
		categoriesButton.click();
		Thread.sleep(2000);
		
		List<WebElement> elementList = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
		int listSize = elementList.size();
		
		Assert.assertTrue("NO CATEGORIES", listSize > 0);
		int random = (int) (Math.random() * listSize);
		//random category
		WebElement randomRow = elementList.get(random);
		//fetch its name
		String name = randomRow.findElement(By.xpath(".//td[2]")).getText();
		//select category
		WebElement checkBox = randomRow.findElement(By.xpath(".//td[1]"));
		checkBox.click();
		Thread.sleep(1000);

		//delete operation
		WebElement deleteButton = driver.findElement(By
						.xpath("//div[contains(@class, 'delete_category')]"));
		deleteButton.click();
		Thread.sleep(1000);
		
		//now try to find it
		elementList = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
		List<String> namesList = new ArrayList<>();
		for (WebElement el : elementList) {
			String elName = el.findElement(By.xpath(".//td[2]")).getText();
			namesList.add(elName);
		}
		Assert.assertTrue("NO SUCH CATEGORY", !namesList.contains(name));
		Thread.sleep(2000);
	}
	
	@Test
	public void createCategory() throws InterruptedException {
		final String newName = "New Category";
		driver.get(BASE_URL);
		Thread.sleep(2000);
		
		//go to category view
		WebElement categoriesButton = driver.findElement(By
				.xpath("//span[@class='v-button-caption'][text()='Categories']"));
		categoriesButton.click();
		Thread.sleep(2000);
		
		//click create category button
		driver.findElement(By.xpath("//div[contains(@class, 'create_category')]")).click();
		Thread.sleep(1000);
		
		WebElement categoryForm = driver.findElement(By
				.xpath("//div[@class='v-formlayout v-layout v-widget']/table/tbody"));

		//find name row
		WebElement nameRow = categoryForm.findElement(By
				.xpath(".//tr[./td/@class='v-formlayout-captioncell'][contains(./td/div/span/text(), 'Name')]"));
		//send something to name field
		nameRow.findElement(By
				.xpath(".//td[@class='v-formlayout-contentcell']/input")).sendKeys(newName);
		Thread.sleep(1000);
		
		//save new category
		driver.findElement(By.xpath("//div[contains(@class, 'edit_category')]")).click();
		Thread.sleep(1000);

		//now try to find it
		List<WebElement> elementList = driver.findElements(By
				.xpath("//tbody[@class='v-grid-body']/tr[contains(@class, 'v-grid-row')]"));
		List<String> namesList = new ArrayList<>();
		for (WebElement el : elementList) {
			String elName = el.findElement(By.xpath(".//td[2]")).getText();
			namesList.add(elName);
		}
		Assert.assertTrue("NO SUCH CATEGORY", namesList.contains(newName));
		Thread.sleep(2000);
		
	}
}
