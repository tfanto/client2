package com.fnt.user;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.sys.RestResponse;

public class UserRepository {

	private static final String REST_USER_END_POINT = "http://localhost:8080/server/rest/user";

	public Client createClient() {

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

	public RestResponse<User> getById(Long id) {

		String idStr = String.valueOf(id);

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_USER_END_POINT).path(idStr).request(MediaType.APPLICATION_JSON)
					.get(Response.class);
			int status = response.getStatus();
			if (status == 200) {
				User data = response.readEntity(new GenericType<User>() {
				});
				return new RestResponse<>(status, data);
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(404, formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<User> create(User user) {
		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_USER_END_POINT).request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(user, MediaType.APPLICATION_JSON), Response.class);
			int status = response.getStatus();
			if (status == 200) {
				User data = response.readEntity(new GenericType<User>() {
				});
				return new RestResponse<>(status, data);
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<User> update(User user) {
		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_USER_END_POINT).request(MediaType.APPLICATION_JSON)
					.put(Entity.entity(user, MediaType.APPLICATION_JSON), Response.class);
			int status = response.getStatus();
			if (status == 200) {
				User data = response.readEntity(new GenericType<User>() {
				});
				return new RestResponse<>(status, data);
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<User> delete(User user) {

		String idStr = String.valueOf(user.getId());

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_USER_END_POINT).path(idStr).request(MediaType.APPLICATION_JSON)
					.delete();
			int status = response.getStatus();
			if (status != 200) {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(status, formatAppMsg(appMsg));
			} else {
				return new RestResponse<>(status);
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}

	}

	private String formatAppMsg(String appMsg) {
		if (appMsg == null)
			return null;
		if (appMsg.startsWith("["))
			appMsg = appMsg.substring(1);
		if (appMsg.endsWith("]"))
			appMsg = appMsg.substring(0, appMsg.length() - 1);
		String parts[] = appMsg.split(",");
		String ret = "";
		for (int i = 0; i < parts.length; i++) {
			ret += parts[i];
			ret += "\n";
		}
		return ret;
	}

	public RestResponse<List<User>> search(String firstName, String lastName, String email, String sortorder) {

		Encoder encoder = Base64.getEncoder();

		String fName = encoder.encodeToString(firstName.getBytes());
		String lName = encoder.encodeToString(lastName.getBytes());
		String em = encoder.encodeToString(email.getBytes());

		String so = encoder.encodeToString(sortorder.getBytes());

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_USER_END_POINT).path("search").queryParam("firstname", fName)
					.queryParam("lastname", lName).queryParam("email", em).queryParam("sortorder", so)
					.request(MediaType.APPLICATION_JSON).get(Response.class);
			int status = response.getStatus();
			if (status == 200) {
				List<User> theList = response.readEntity(new GenericType<List<User>>() {
				});
				return new RestResponse<>(status, theList);
			} else {
				return new RestResponse<>(200, new ArrayList<>());
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

}
