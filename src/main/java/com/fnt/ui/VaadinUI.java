package com.fnt.ui;

import javax.servlet.annotation.WebServlet;

import org.vaadin.teemusa.sidemenu.SideMenu;

import com.fnt.authentication.AppLoginForm;
import com.fnt.authentication.AppLoginRepository;
import com.fnt.customer.CustomerList;
import com.fnt.customerorder.CustomerOrderList;
import com.fnt.item.ItemList;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
@PushStateNavigation
public class VaadinUI extends UI {
	
	
	private SideMenu sideMenu = new SideMenu();
	private boolean logoVisible = true;
	private ThemeResource logo = new ThemeResource("images/linux-penguin.png");
	private String menuCaption = "T C O";

	@Override
	protected void init(VaadinRequest request) {
		setContent(sideMenu);
		Navigator navigator = new Navigator(this, sideMenu);
		setNavigator(navigator);

		// NOTE: Navigation and custom code menus should not be mixed.
		// See issue #8

		navigator.addView("", DefaultView.class);
		navigator.addView("Customer", CustomerList.class);
		navigator.addView("Item", ItemList.class);
		navigator.addView("Order", CustomerOrderList.class);
		navigator.addView("Login", AppLoginForm.class);

		sideMenu.setMenuCaption(menuCaption, logo);

		// Navigation examples
		sideMenu.addNavigation("Customer", VaadinIcons.AMBULANCE, "Customer");
		sideMenu.addNavigation("Item", VaadinIcons.AMBULANCE, "Item");
		sideMenu.addNavigation("Customer order", VaadinIcons.AMBULANCE, "Order");

		if (!AppLoginRepository.isAuthenticated()) {
			navigator.navigateTo("Login");
		}

		navigator.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {

				if (AppLoginRepository.isAuthenticated()) {
					return true;
				}
				else {
					Notification.show("Forbidden", "User not authenticated", Notification.Type.TRAY_NOTIFICATION);
				}
				return false;
			}
		});

		if (AppLoginRepository.isAuthenticated()) {
			
			Object userObj = VaadinSession.getCurrent().getAttribute("login");
			String user = "";
			if(userObj instanceof String) {
				user = String.valueOf(userObj);
			}
			setUser(user, VaadinIcons.HANDSHAKE);
		}

	}

	private void setUser(String name, Resource icon) {
		sideMenu.setUserName(name);
		sideMenu.setUserIcon(icon);
		sideMenu.clearUserMenu();
		sideMenu.addUserMenuItem("Sign out", () -> {
			logout();
		});
	}

	protected void inixxxt(VaadinRequest request) {

		// https://vaadin.com/directory/component/sidemenu-add-on

		// https://github.com/tsuoanttila/sidemenu-addon/blob/master/sidemenu-demo/src/main/java/org/vaadin/teemusa/sidemenu/demo/DemoUI.java

	}

	// @formatter:off
	/*
	@Override
	protected void init(VaadinRequest request) {

		Label title = new Label("Menu");
		title.addStyleName(ValoTheme.MENU_TITLE);

		Button view1 = new Button("Customer", e -> getNavigator().navigateTo("Customer"));
		view1.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button view2 = new Button("Item", e -> getNavigator().navigateTo("Item"));
		view2.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button view3 = new Button("Order", e -> getNavigator().navigateTo("Order"));
		view3.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button btnLogout = new Button("Logout", e -> logout());
		btnLogout.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		CssLayout menu = new CssLayout(title, view1, view2, view3, btnLogout);
		menu.addStyleName(ValoTheme.MENU_ROOT);
		menu.setWidth("200px");

		CssLayout viewContainer = new CssLayout();
		viewContainer.setSizeFull();
		HorizontalLayout mainLayout = new HorizontalLayout(menu, viewContainer);
		mainLayout.setExpandRatio(menu, 0);
		mainLayout.setExpandRatio(viewContainer, 1);

		mainLayout.setSizeFull();
		setContent(mainLayout);

		Navigator navigator = new Navigator(this, viewContainer);
		navigator.addView("", DefaultView.class);
		navigator.addView("Customer", CustomerList.class);
		navigator.addView("Item", ItemList.class);
		navigator.addView("Order", CustomerOrderList.class);
		navigator.addView("Login", AppLoginForm.class);

		if (!AppLoginRepository.isAuthenticated()) {
			view1.setVisible(false);
			view2.setVisible(false);
			view3.setVisible(false);
			btnLogout.setVisible(false);
			navigator.navigateTo("Login");
		} else {
			view1.setVisible(true);
			view2.setVisible(true);
			view3.setVisible(true);
			btnLogout.setVisible(true);

		}
		navigator.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {

				if (AppLoginRepository.isAuthenticated()) {
					return true;
				}
				return false;
			}
		});

	}
	
	*/
	// @formatter:on

	private Object logout() {
		AppLoginRepository.logout();
		return null;
	}

	@WebServlet(urlPatterns = "/*", name = "VaadinUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
	public static class VaadinUIServlet extends VaadinServlet {
	}

	
	
	
	

	// @formatter:off

	/*
	@Override
	protected void init(VaadinRequest request) {

		Label title = new Label("Menu");
		title.addStyleName(ValoTheme.MENU_TITLE);

		Button view1 = new Button("Customer", e -> getNavigator().navigateTo("Customer"));
		view1.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button view2 = new Button("Item", e -> getNavigator().navigateTo("Item"));
		view2.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button view3 = new Button("Order", e -> getNavigator().navigateTo("Order"));
		view3.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		Button btnLogout = new Button("Logout", e -> logout());
		btnLogout.addStyleNames(ValoTheme.BUTTON_LINK, ValoTheme.MENU_ITEM);

		CssLayout menu = new CssLayout(title, view1, view2, view3, btnLogout);
		menu.addStyleName(ValoTheme.MENU_ROOT);
		menu.setWidth("200px");

		CssLayout viewContainer = new CssLayout();
		viewContainer.setSizeFull();
		HorizontalLayout mainLayout = new HorizontalLayout(menu, viewContainer);
		mainLayout.setExpandRatio(menu, 0);
		mainLayout.setExpandRatio(viewContainer, 1);

		mainLayout.setSizeFull();
		setContent(mainLayout);

		Navigator navigator = new Navigator(this, viewContainer);
		navigator.addView("", DefaultView.class);
		navigator.addView("Customer", CustomerList.class);
		navigator.addView("Item", ItemList.class);
		navigator.addView("Order", CustomerOrderList.class);
		navigator.addView("Login", AppLoginForm.class);

		if (!AppLoginRepository.isAuthenticated()) {
			view1.setVisible(false);
			view2.setVisible(false);
			view3.setVisible(false);
			btnLogout.setVisible(false);
			navigator.navigateTo("Login");
		} else {
			view1.setVisible(true);
			view2.setVisible(true);
			view3.setVisible(true);
			btnLogout.setVisible(true);

		}
		navigator.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {

				if (AppLoginRepository.isAuthenticated()) {
					return true;
				}
				return false;
			}
		});

	}

	private Object logout() {
		AppLoginRepository.logout();
		return null;
	}

	@WebServlet(urlPatterns = "/*", name = "VaadinUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
	public static class VaadinUIServlet extends VaadinServlet {
	}
	
	*/
	
	// @formatter:on

}
