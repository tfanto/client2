package com.fnt.role;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class RoleRepository {

	private static final String REST_ROLE_END_POINT = "http://localhost:8080/server/rest/role";

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

	public List<Role> findAll() {

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_ROLE_END_POINT).request(MediaType.APPLICATION_JSON)
					.get(Response.class);
			int status = response.getStatus();
			if (status == 200) {
				List<Role> theList = response.readEntity(new GenericType<List<Role>>() {
				});
				return theList;
			} else {
				return new ArrayList<>();
			}

		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public Role save(Role role) {

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_ROLE_END_POINT).request(MediaType.APPLICATION_JSON)
					.post(Entity.entity(role, "application/json"));
			int status = response.getStatus();
			if (status == 200) {
				Role data = response.readEntity(new GenericType<Role>() {
				});
				return data;
			} else {
				return null;
			}

		} finally {
			if (client != null) {
				client.close();
			}
		}

	}

}
