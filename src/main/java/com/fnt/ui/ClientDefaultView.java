package com.fnt.ui;

import java.io.File;

import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ClientDefaultView extends Composite implements View {

	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private FileResource picture = new FileResource(new File(basepath + "/WEB-INF/images/tf03.jpg"));

	public ClientDefaultView() {

		Image image = new Image("", picture);

		VerticalLayout layout = new VerticalLayout();
		Label lblCopyright = new Label("Fanto Software Engineering (c) 2018");
		Label lblContact = new Label("For more info:");
		Label lblContact1 = new Label("phone: +46 70 980 5011");
		Label lblContact2 = new Label("mail : tomasfanto@gmail.com ");

		layout.addComponent(lblCopyright);
		layout.addComponent(image);
		layout.addComponent(lblContact);
		layout.addComponent(lblContact1);
		layout.addComponent(lblContact2);
		layout.setComponentAlignment(lblCopyright, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(image, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(lblContact, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(lblContact1, Alignment.MIDDLE_CENTER);
		layout.setComponentAlignment(lblContact2, Alignment.MIDDLE_CENTER);

		setCompositionRoot(layout);
	}

}
