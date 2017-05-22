package com.demo.app.hotel.test;

import org.junit.Assert;
import org.junit.Test;

public class HotelsUITest extends AbstractUITest {

	@Test
	public void testHotelPage() throws InterruptedException {
		driver.get(BASE_URL);
		Thread.sleep(1000);
		
		Assert.assertEquals("http://localhost:8080/#!hotels", driver.getCurrentUrl());
	}
}
