package com.demo.app.hotel.ui;

import javax.servlet.annotation.WebServlet;

import com.demo.app.hotel.ui.views.CategoriesView;
import com.demo.app.hotel.ui.views.HotelsView;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.SpringViewDisplay;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;

@Theme("mytheme")
@SpringUI
@SpringViewDisplay
public class HotelsUI extends UI implements ViewDisplay {

    private HorizontalLayout menuAndContent;
    private CssLayout menu;
    private Button hotelBtn, categoryBtn;
    private Panel content = new Panel();

    @Autowired
	SpringViewProvider viewProvider;

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setUpPageForms();
        setUpPageLayout();

        Navigator navigator = new Navigator(this, content);
		navigator.addProvider(viewProvider);

		getUI().getNavigator().navigateTo(HotelsView.VIEW_NAME);
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
        hotelBtn.setIcon(FontAwesome.TASKS);
        hotelBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		hotelBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
		categoryBtn = new Button("Categories");
		categoryBtn.addStyleName("menubutton");
		categoryBtn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
		categoryBtn.addStyleName(ValoTheme.BUTTON_ICON_ALIGN_TOP);
		categoryBtn.setIcon(FontAwesome.TASKS);

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
}
