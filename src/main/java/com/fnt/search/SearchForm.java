package com.fnt.search;

import java.util.List;

import com.fnt.customerorder.CustomerOrderRepository;
import com.fnt.dto.SearchData;
import com.fnt.sys.Fnc;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderRow;

public class SearchForm extends Window {

	public static final int CUSTOMERS = 1;
	public static final int ITEMS = 2;
	private int searchType = Integer.MIN_VALUE;
	private CustomerOrderRepository searchRepository;
	private TextField search1 = new TextField();
	private TextField search2 = new TextField();

	private static final long serialVersionUID = 1L;

	private Fnc fnc = new Fnc();

	private Button btn_cancel = new Button("Cancel");
	private Button btn_search = new Button("Search", VaadinIcons.SEARCH);
	private Button btn_select = new Button("Select", VaadinIcons.CHECK);

	private Grid<SearchData> grid = new Grid<>(SearchData.class);

	public SearchForm(int searchType, TextField searchField1, TextField searchField2, CustomerOrderRepository searchRepository) {
		this.searchRepository = searchRepository;
		search1.setValue(searchField1.getValue());
		search2.setValue(searchField2.getValue());

		this.searchType = searchType;
		switch (searchType) {
		case CUSTOMERS:
			break;
		case ITEMS:
			break;

		default: {
			searchType = 1;
		}
		}

		initLayout();
		initBehavior();

	}

	private void initLayout() {

		grid.setColumns("id", "description");

		HeaderRow headerRow = grid.getDefaultHeaderRow();
		headerRow.getCell("id").setComponent(fnc.createFilterField(search1));
		headerRow.getCell("description").setComponent(fnc.createFilterField(search2));
		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_search, btn_select);
		buttons.setSpacing(true);
		VerticalLayout layout = new VerticalLayout();
		layout.addComponents(grid, buttons);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior() {

		btn_cancel.addClickListener(e -> close());
		btn_search.addClickListener(e -> search());
		btn_select.addClickListener(e -> select());

	}

	private Object select() {
		// TODO Auto-generated method stub
		return null;
	}

	private Object search() {

		switch (searchType) {
		case CUSTOMERS: {
			List<SearchData> resultSet = searchRepository.selectListCustomers(search1.getValue(), search2.getValue());
			grid.setItems(resultSet);
			break;
		}
		case ITEMS: {
			List<SearchData> resultSet = searchRepository.selectListItems(search1.getValue(), search2.getValue());
			grid.setItems(resultSet);
			break;
		}
		}
		return null;
	}

}
