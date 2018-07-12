package com.fnt.item;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.entity.Item;
import com.fnt.sys.RestResponse;

public class ItemRepository {

	private static final String REST_ITEM_END_POINT = "http://localhost:8080/server2/rest/item";

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

	public RestResponse<List<Item>> search(String itemNumberStr, String descriptionStr, String sortorderStr) {

		Encoder encoder = Base64.getEncoder();

		String itemnumber = encoder.encodeToString(itemNumberStr.getBytes());
		String description = encoder.encodeToString(descriptionStr.getBytes());
		String sortorder = encoder.encodeToString(sortorderStr.getBytes());

		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_ITEM_END_POINT).path("search").queryParam("itemnumber", itemnumber)
					.queryParam("description", description).queryParam("sortorder", sortorder)
					.request(MediaType.APPLICATION_JSON).get(Response.class);
			int status = response.getStatus();
			if (status == 200) {
				List<Item> theList = response.readEntity(new GenericType<List<Item>>() {
				});
				return new RestResponse<>(status, theList);
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

	public RestResponse<Item> getById(Long id) {
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
