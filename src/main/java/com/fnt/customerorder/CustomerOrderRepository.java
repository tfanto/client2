package com.fnt.customerorder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.fnt.customer.CustomerRepository;
import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.dto.SearchData;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.Item;
import com.fnt.item.ItemRepository;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;

public class CustomerOrderRepository {

	private Fnc fnc = new Fnc();

	private String REST_CUSTOMER_ORDER_END_POINT = "http://localhost:8080/server2/rest/customerorder";
	
	private CustomerRepository customerRepository = new CustomerRepository();
	private ItemRepository itemRepository = new ItemRepository();

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

	public RestResponse<List<CustomerOrderHeadListView>> search(String filterCustomerNumberStr, String filterNameStr, LocalDate filterDate, String filterStatusStr, String filterChangedByStr, String sortOrderStr) {
		Encoder encoder = Base64.getUrlEncoder();

		String customernumber = encoder.encodeToString(filterCustomerNumberStr.getBytes());
		String name = encoder.encodeToString(filterNameStr.getBytes());

		String formattedDateString = "";
		if (filterDate != null) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			formattedDateString = filterDate.format(formatter);
		}

		String orderstatus = encoder.encodeToString(filterStatusStr.getBytes());
		String changedby = encoder.encodeToString(filterChangedByStr.getBytes());
		String sortorder = encoder.encodeToString(sortOrderStr.getBytes());
		String theDate = encoder.encodeToString(formattedDateString.getBytes());

		// @formatter:off
		Client client = null;
		try {
			client = createClient();
			Response response = client.target(REST_CUSTOMER_ORDER_END_POINT).path("search")
					.queryParam("customernumber", customernumber)
					.queryParam("name", name)
					.queryParam("date", theDate)
					.queryParam("orderstatus", orderstatus)
					.queryParam("changedby", changedby)
					.queryParam("sortorder", sortorder)
					.request(MediaType.APPLICATION_JSON).get(Response.class);
			// @formatter:on
			int status = response.getStatus();
			if (status == 200) {
				List<CustomerOrderHeadListView> theList = response.readEntity(new GenericType<List<CustomerOrderHeadListView>>() {
				});
				return new RestResponse<>(status, theList);
			} else {
				JsonNode jsonNode = response.readEntity(JsonNode.class);
				String appMsg = jsonNode.path("appMsg").textValue();
				return new RestResponse<>(404, fnc.formatAppMsg(appMsg));
			}
		} finally {
			if (client != null) {
				client.close();
			}
		}
	}

	public RestResponse<CustomerOrderHead> getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Item> create(CustomerOrderHead obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Item> update(CustomerOrderHead obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Item> delete(CustomerOrderHead obj) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<List<SearchData>> selectListCustomers(String value, String value2) {
		return customerRepository.selectList(value, value2);
	}

	public RestResponse<List<SearchData>> selectListItems(String value, String value2) {
		return itemRepository.selectList(value, value2);
	}

}
