package com.fnt.ui;

import javax.servlet.annotation.WebServlet;

import com.fnt.user.UserList;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Component;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class VaadinUI extends UI {

	private TabSheet tabSheet = new TabSheet();

	@Override
	protected void init(VaadinRequest request) {

		tabSheet.setSizeFull();
		setContent(tabSheet);
		addTab(new UserList());

	}

	private void addTab(Component content) {
		tabSheet.addTab(content, "User");
	}

	@WebServlet(urlPatterns = "/*", name = "VaadinUIServlet", asyncSupported = true)
	@VaadinServletConfiguration(ui = VaadinUI.class, productionMode = false)
	public static class VaadinUIServlet extends VaadinServlet {
	}

}
