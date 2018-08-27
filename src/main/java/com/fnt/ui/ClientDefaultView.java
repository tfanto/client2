package com.fnt.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import com.fnt.broadcasting.BroadcastingData;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.navigator.View;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServlet;
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

	private Client client;
	private WebTarget webTarget;
	private SseEventSource eventSource;
	private static final String REST_EVENT_END_POINT = String.valueOf(VaadinServlet.getCurrent().getServletContext().getAttribute("REST_EVENT_END_POINT"));

	public ClientDefaultView() {

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

		ListDataProvider<BroadcastingData> dataProvider = DataProvider.ofCollection(notifications);
		grid.setDataProvider(dataProvider);

		client = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();

		webTarget = client.target(REST_EVENT_END_POINT);
		try {
			eventSource = SseEventSource.target(webTarget).reconnectingEvery(1, TimeUnit.SECONDS).build();
			eventSource.register(this::onMessage, this::onError);
			eventSource.open();
		} catch (Throwable t) {
			System.out.println(t.toString());
		} finally {
		}

		System.out.println("--------------------------------------------------------------------------------------------------------------- CTOR");

		setCompositionRoot(layout);
	}

	void onMessage(InboundSseEvent event) {
		System.out.println("--------------------------------------------------------------------------------------------------------------- ON_MSG");

		String id = event.getId();
		String name = event.getName();
		String payload = event.readData();
		String comment = event.getComment();
		// processing...
		BroadcastingData data = new BroadcastingData();
		data.setData(payload);
		notifications.add(data);

	}

	void onError(Throwable t) {
		System.out.println("--------------------------------------------------------------------------------------------------------------- ON_ERROR");

		t.printStackTrace();

	}

	@Override
	public void finalize() {
		System.out.println("--------------------------------------------------------------------------------------------------------------- DTOR");

		eventSource.close();

	}

}