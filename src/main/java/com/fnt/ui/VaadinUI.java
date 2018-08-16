package com.fnt.ui;

import java.io.File;

import javax.servlet.annotation.WebServlet;

import org.vaadin.teemusa.sidemenu.SideMenu;

import com.fnt.authentication.AppLoginForm;
import com.fnt.authentication.AppPasswordUpdateForm;
import com.fnt.authentication.AppLoginRepository;
import com.fnt.customer.CustomerList;
import com.fnt.customerorder.CustomerOrderList;
import com.fnt.item.ItemList;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.PushStateNavigation;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@PushStateNavigation
public class VaadinUI extends UI {

	private SideMenu sideMenu = new SideMenu();
	private boolean logoVisible = true;
	private String menuCaption = "T C O";

	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();

	// Image as a file resource
	private FileResource logo = new FileResource(new File(basepath + "/WEB-INF/images/fire.jpg"));
	private FileResource cuno = new FileResource(new File(basepath + "/WEB-INF/images/customer.jpg"));
	private FileResource itno = new FileResource(new File(basepath + "/WEB-INF/images/item.jpg"));
	private FileResource cunoorder = new FileResource(new File(basepath + "/WEB-INF/images/customerorder.jpg"));

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

		sideMenu.addNavigation("Customer", cuno, "Customer");
		sideMenu.addNavigation("Item", itno, "Item");
		sideMenu.addNavigation("Customer order", cunoorder, "Order");

		if (!AppLoginRepository.isAuthenticated()) {
			navigator.navigateTo("Login");
		}

		navigator.addViewChangeListener(new ViewChangeListener() {
			@Override
			public boolean beforeViewChange(ViewChangeEvent event) {

				if (AppLoginRepository.isAuthenticated()) {
					return true;
				} else {
					if (event.getNewView().getClass().equals(AppLoginForm.class)) {
						return true;
					}
				}
				Notification.show("User not authenticated", Notification.Type.WARNING_MESSAGE);
				return false;
			}
		});

		if (AppLoginRepository.isAuthenticated()) {

			Object userObj = VaadinSession.getCurrent().getAttribute("login");
			String user = "";
			if (userObj instanceof String) {
				user = String.valueOf(userObj);
			}
			setUser(user);
		}

	}
	
	private FileResource getUserIcon(String user) {
		
		FileResource resource = null;
		String userResource = basepath + "/WEB-INF/images/"+ user + ".jpg";
		File f = new File(userResource);
		if(f.exists()) {
			resource = new FileResource(f);
		}
		else {
			resource = new FileResource(new File(basepath + "/WEB-INF/images/profilDummy.jpg"));			
		}
		return resource;
	}

	private void setUser(String user) {
		
		FileResource resource = getUserIcon(user);
		
		sideMenu.setUserName(user);
		sideMenu.setUserIcon(resource);
		sideMenu.clearUserMenu();
		sideMenu.addUserMenuItem("Update password", () -> {
			AppPasswordUpdateForm window = new AppPasswordUpdateForm();
			getUI().addWindow(window);
		});
		sideMenu.addUserMenuItem("Sign out", () -> {
			logout();
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

	// @formatter:on

}
