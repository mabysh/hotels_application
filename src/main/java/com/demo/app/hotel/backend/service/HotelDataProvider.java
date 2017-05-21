package com.demo.app.hotel.backend.service;

import java.util.List;
import java.util.stream.Stream;

import com.demo.app.hotel.backend.entity.Hotel;
import com.demo.app.hotel.backend.service.ApplicationServiceImpl;
import com.demo.app.hotel.backend.service.ServiceFactory;
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;

@SuppressWarnings("serial")
public class HotelDataProvider extends AbstractBackEndDataProvider<Hotel, List<String>> {
	
	private ApplicationServiceImpl service;

	public HotelDataProvider() {
			service = ServiceFactory.getApplicationServiceImpl();
	}

	@Override
	protected Stream<Hotel> fetchFromBackEnd(Query<Hotel, List<String>> query) {
		List<String> filter = query.getFilter().get();
		List<Hotel> hotels = ServiceFactory.getApplicationServiceImpl().findAllHotels(filter); 
		return hotels.stream();	

	}

	@Override
	protected int sizeInBackEnd(Query<Hotel, List<String>> query) {
		List<String> filter = query.getFilter().get();
		List<Hotel> hotels = service.findAllHotels(filter);
		return hotels.size();	
	}

}
