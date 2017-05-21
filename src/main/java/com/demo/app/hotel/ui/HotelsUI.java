package com.demo.app.hotel.ui;

import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;

import com.demo.app.hotel.backend.service.ServiceFactory;
import com.demo.app.hotel.ui.views.CategoriesView;
import com.demo.app.hotel.ui.views.HotelsView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextLoaderListener;

@Theme("mytheme")
@SpringUI
//@Widgetset()
@SpringViewDisplay
public class HotelsUI extends UI implements ViewDisplay {

    private HorizontalLayout menuAndContent;
    private CssLayout menu;
    private Button hotelBtn, categoryBtn;
    private Panel content = new Panel();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpPageForms();
        setUpPageLayout();

        ServiceFactory.getApplicationServiceImpl().ensureTestData();

        Navigator navigator = new Navigator(this, content);
		navigator.addView(HotelsView.VIEW_NAME, HotelsView.class);
		navigator.addView(CategoriesView.VIEW_NAME, CategoriesView.class);

		navigator.navigateTo(HotelsView.VIEW_NAME);
    }

	private void setUpPageLayout() {
        menu = new CssLayout();
    	menu.setStyleName("menu");
    	menu.setHeight("100%");
    	menu.addComponents(hotelBtn, categoryBtn);

    	menuAndContent = new HorizontalLayout();
       	menuAndContent.setSizeFull();
		menuAndContent.setSpacing(false);
    	menuAndContent.addComponents(menu, content);
    	menuAndContent.setExpandRatio(menu, 1);
		menuAndContent.setExpandRatio(content,14);

		hotelBtn.addStyleName("menubutton_clicked");
		setContent(menuAndContent);
	}

	private void setUpPageForms() {
        hotelBtn = new Button("Hotels");
		hotelBtn.addStyleName("menubutton");
        hotelBtn.setIcon(VaadinIcons.TASKS);
        hotelBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		hotelBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
		categoryBtn = new Button("Categories");
		categoryBtn.addStyleName("menubutton");
		categoryBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		categoryBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
		categoryBtn.setIcon(VaadinIcons.TASKS);

		hotelBtn.addClickListener(event -> {
			categoryBtn.removeStyleName("menubutton_clicked");
		    hotelBtn.addStyleName("menubutton_clicked");
			getUI().getNavigator().navigateTo(HotelsView.VIEW_NAME);
		});
		categoryBtn.addClickListener(event -> {
			hotelBtn.removeStyleName("menubutton_clicked");
			categoryBtn.addStyleName("menubutton_clicked");
			getUI().getNavigator().navigateTo(CategoriesView.VIEW_NAME);
		});
		content.setSizeFull();
		content.addStyleName("content");
	}

	@Override
	public void showView(View view) {
		content.setContent((Component) view);
	}

    @WebServlet(urlPatterns = "/*", name = "HotelsUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = HotelsUI.class, productionMode = false)
    public static class HotelsUIServlet extends SpringVaadinServlet {
    }

    @WebListener
	public static class MyContextLoaderListener extends ContextLoaderListener {}


	@Configuration
	@EnableVaadin
	public static class HotelDataConfig {
    	@Bean
		public static ServiceFactory getServiceFactory() {
    		return new ServiceFactory();
		}
	}

}
