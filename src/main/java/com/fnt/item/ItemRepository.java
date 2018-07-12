package com.fnt.item;

import java.util.List;

import com.fnt.entity.Customer;
import com.fnt.entity.Item;
import com.fnt.sys.RestResponse;

public class ItemRepository {

	public RestResponse<List<Item>> search(String itemNumberStr, String descriptionStr, String orderingPointStr,
			String inStockStr, String priceStr, String purchasePriceStr, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	public RestResponse<Item> getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}
