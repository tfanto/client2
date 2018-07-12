package com.fnt.customer;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.entity.Customer;
import com.fnt.sys.RestResponse;

public class CustomerRepository {

	private static final String REST_CUSTOMER_END_POINT = "http://localhost:8080/server2/rest/customer";

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

	public RestResponse<List<Customer>> search(String customernumber, String name, String sortorder) {
		Encoder encoder = Base64.getEncoder();

		String cn = encoder.encodeToString(customernumber.getBytes());
		String n = encoder.encodeToString(name.getBytes());

		String so = encoder.encodeToString(sortorder.getBytes());

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_CUSTOMER_END_POINT).path("search").queryParam("customernumber", cn)
					.queryParam("name", n).queryParam("sortorder", so).request(MediaType.APPLICATION_JSON)
					.get(Response.class);
			int status = response.getStatus();
			if (status == 200) {
				List<Customer> theList = response.readEntity(new GenericType<List<Customer>>() {
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

	public RestResponse<Customer> getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Customer> create(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Customer> update(Customer customer) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Customer> delete(Customer customer) {
		// TODO Auto-generated method stub
		return null;
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

}
