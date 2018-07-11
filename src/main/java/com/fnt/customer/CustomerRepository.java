package com.fnt.customer;

import java.util.List;

import com.fnt.entity.Customer;
import com.fnt.sys.RestResponse;

public class CustomerRepository {

	public RestResponse<List<Customer>> search(String firstNameStr, String lastNameStr, String emailStr,
			String sortOrder) {
		// TODO Auto-generated method stub
		return null;
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

}
