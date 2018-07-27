package com.fnt.ui;

import javax.servlet.annotation.WebServlet;

import com.fnt.customer.CustomerList;
import com.fnt.customerorder.CustomerOrderList;
import com.fnt.item.ItemList;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@PushStateNavigation
public class VaadinUI extends UI {


	@Override
	protected void init(VaadinRequest request) {


		Label title = new Label("Menu");
		title.addStyleName(ValoTheme.MENU_TITLE);

		Button view1 = new Button("Customer", e -> getNavigator().navigateTo("view1"));
		view1.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button view2 = new Button("Item", e -> getNavigator().navigateTo("view2"));
		view2.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button view3 = new Button("Order", e -> getNavigator().navigateTo("view3"));
		view3.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		CssLayout menu = new CssLayout(title, view1, view2,view3);
		menu.addStyleName(ValoTheme.MENU_ROOT);

		CssLayout viewContainer = new CssLayout();
		viewContainer.setSizeFull();
		HorizontalLayout mainLayout = new HorizontalLayout(menu, viewContainer);
		mainLayout.setExpandRatio(menu, 0);
		mainLayout.setExpandRatio(viewContainer, 1);


		mainLayout.setSizeFull();
		setContent(mainLayout);

		Navigator navigator = new Navigator(this, viewContainer);
		navigator.addView("", DefaultView.class);
		navigator.addView("view1", CustomerList.class);
		navigator.addView("view2", ItemList.class);
		navigator.addView("view3", CustomerOrderList.class);

	}


	@WebServlet(urlPatterns = "/*", name = "VaadinUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
	public static class VaadinUIServlet extends VaadinServlet {
	}

}
