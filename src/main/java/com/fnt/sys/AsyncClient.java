package com.fnt.sys;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.ws.rs.container.AsyncResponse;


@ClientEndpoint
public class AsyncClient {

	private final String asyncServer = "ws://localhost:8080/server2/rest/asyncServer";

	private Session session;
	private final AsyncResponse response;

	public AsyncClient() {
		response = null;
	}

	public AsyncClient(AsyncResponse response) {
		this.response = response;
	}

	public void connect() {
		WebSocketContainer container = ContainerProvider.getWebSocketContainer();
		try {
			container.connectToServer(this, new URI(asyncServer));
		} catch (URISyntaxException | DeploymentException | IOException ex) {
			System.err.println(ex.getMessage());
		}

	}

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		
		System.out.println(message);
		
		response.resume(message);
	}


	public void send(String message) {
		session.getAsyncRemote().sendText(message);
	}

	public void close() {
		try {
			session.close();
		} catch (IOException ex) {
			System.err.println(ex.getMessage());
		}
	}
}
