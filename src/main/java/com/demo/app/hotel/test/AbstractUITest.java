package com.demo.app.hotel.test;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class AbstractUITest {
	
	protected WebDriver driver; 
	protected static final String BASE_URL = "http://localhost:8080";
	
	public AbstractUITest() {
		System.setProperty("webdriver.gecko.driver", "/usr/bin/geckodriver");
	}

	@Before
	public void initDriver() throws InterruptedException {
		driver = new FirefoxDriver();
	}
	
	@After
	public void tearDown() throws InterruptedException {
		Thread.sleep(1000);
		driver.quit();
	}
}
