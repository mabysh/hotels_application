package com.demo.app.hotel.backend.config;

import com.vaadin.spring.annotation.EnableVaadin;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.demo.app.hotel")
@EnableVaadin
public class HotelDataConfig { }
