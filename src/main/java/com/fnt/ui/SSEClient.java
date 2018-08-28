package com.fnt.ui;

import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.sse.InboundSseEvent;
import javax.ws.rs.sse.SseEventSource;

import com.fnt.broadcasting.BroadcastingData;

public class SSEClient {

	String resteventEndpoint;

	private WebTarget target;
	private SseEventSource eventSource;

	public SSEClient(String resteventEndpoint) {
		this.resteventEndpoint = resteventEndpoint;
	}

	public void open() {
		// @formatter:off
		/*
		Client client  = ClientBuilder
				.newBuilder()
				.connectTimeout(15, TimeUnit.SECONDS)
				.readTimeout(30, TimeUnit.SECONDS)
				.build();
		client.target(resteventEndpoint);
		*/
		// @formatter:on

		// sseClient = ClientBuilder.newClient();
		target = ClientBuilder.newClient().target(resteventEndpoint);
		try {
			eventSource = SseEventSource.target(target).reconnectingEvery(5, TimeUnit.SECONDS).build();
			eventSource.register(this::onMessage, this::onError);
			if (!eventSource.isOpen()) {
				eventSource.open();
			}
		} catch (Throwable t) {
			System.out.println(t.toString());
		}

	}

	public void addNotification(BroadcastingData data) {
		
		System.out.println(data.getData());
	}

	public void onMessage(InboundSseEvent event) {
		String id = event.getId();
		String name = event.getName();
		String payload = event.readData();
		String comment = event.getComment();
		// processing...
		BroadcastingData data = new BroadcastingData();
		data.setData(payload);
		addNotification(data);

	}

	public void onError(Throwable t) {
		t.printStackTrace();

	}

	public void close() {
		if (eventSource != null)
			eventSource.close();
	}

}
