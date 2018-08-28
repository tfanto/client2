package com.fnt.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fnt.broadcasting.BroadcastingData;
import com.fnt.sys.AppClientServletContextListener;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ClientDefaultView extends Composite implements View {

	private String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	private FileResource picture = new FileResource(new File(basepath + "/WEB-INF/images/tf03.jpg"));


	public Grid<BroadcastingData> grid = new Grid<>();
	private List<BroadcastingData> notifications = new ArrayList<>();
	ListDataProvider<BroadcastingData> dataProvider;

	
	@Subscribe
	public void stringEvent(String event) {
		BroadcastingData d = new BroadcastingData();
		d.setData(event);
		notifications.add(d);
		dataProvider.refreshAll();
	}

	public ClientDefaultView() {
		
		AppClientServletContextListener.getSSE().addSubscriber(this);

		grid.removeAllColumns();

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

		grid.addColumn(BroadcastingData::getData).setExpandRatio(1).setId("data").setCaption("Notifications");
		for (Column<BroadcastingData, ?> col : grid.getColumns()) {
			col.setSortable(false);
		}

		layout.addComponent(grid);
		layout.setComponentAlignment(grid, Alignment.BOTTOM_CENTER);

		dataProvider = DataProvider.ofCollection(notifications);
		grid.setDataProvider(dataProvider);

		setCompositionRoot(layout);
	}

	@Override
	public void finalize() {
		AppClientServletContextListener.getSSE().removeSubscriber(this);
	}

}