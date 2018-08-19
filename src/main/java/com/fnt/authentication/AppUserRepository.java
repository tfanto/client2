package com.fnt.authentication;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.entity.AppUser;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

public class AppUserRepository {

	private static final String LOGIN = "login";

	private static final String USER_REGISTRATION_END_POINT = String.valueOf(VaadinServlet.getCurrent().getServletContext().getAttribute("USER_REGISTRATION_END_POINT"));
	private static final String APPUSER_END_POINT = String.valueOf(VaadinServlet.getCurrent().getServletContext().getAttribute("APPUSER_END_POINT"));

	private static Client createClient() {

		Client client = ClientBuilder.newClient();
		client.register(new ContextResolver<ObjectMapper>() {
			@Override
			public ObjectMapper getContext(Class<?> type) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JavaTimeModule());
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				return mapper;
			}
		});
		return client;
	}

	/**
	 * warning this goes to the security server
	 * 
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public static RestResponse<Boolean> updatePassword(String oldPassword, String newPassword) {

		Fnc fnc = new Fnc();

		Object obj = VaadinSession.getCurrent().getAttribute(LOGIN);
		if (!(obj instanceof String)) {
			return new RestResponse<>(400, "Invalid or not authenticated login.");
		}
		String login = String.valueOf(obj);

		Map<String, String> payLoad = new HashMap<>();
		payLoad.put("login", login);
		payLoad.put("oldpwd", oldPassword);
		payLoad.put("newpwd", newPassword);

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(USER_REGISTRATION_END_POINT)
					.path("updpwd")
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.put(Entity.json(payLoad), Response.class);
			int status = response.getStatus();
			// @formatter:on
			if (status == 200) {
				return new RestResponse<>(status, true);
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}

	}

	public static RestResponse<Boolean> updateAppUser(AppUser user) {
		Fnc fnc = new Fnc();

		Object obj = VaadinSession.getCurrent().getAttribute(LOGIN);
		if (!(obj instanceof String)) {
			return new RestResponse<>(400, "Invalid or not authenticated login.");
		}
		String login = String.valueOf(obj);

		user.setLogin(login);
		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(APPUSER_END_POINT)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.post(Entity.json(user), Response.class);
			int status = response.getStatus();
			// @formatter:on
			if (status == 200) {
				return new RestResponse<>(status, true);
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public static RestResponse<AppUser> get(String login) {
		Fnc fnc = new Fnc();

		Object obj = VaadinSession.getCurrent().getAttribute(LOGIN);
		if (!(obj instanceof String)) {
			return new RestResponse<>(400, "Invalid or not authenticated login.");
		}

		Client client = null;
		try {
			client = createClient();
			// @formatter:off
			Response response = client
					.target(APPUSER_END_POINT)
					.path(login)
					.request(MediaType.APPLICATION_JSON)
					.header("Authorization", fnc.getToken(VaadinSession.getCurrent()))					
					.get( Response.class);
			int status = response.getStatus();
			// @formatter:on
			if (status == 200) {
				AppUser fetched = response.readEntity(AppUser.class);
				return new RestResponse<>(status, fetched);
			} else if (status == 403) {
				return new RestResponse<>(status, response.getStatusInfo().toString());
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

}
