package com.fnt.search;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.List;

import com.fnt.customerorder.CustomerOrderRepository;
import com.fnt.dto.SearchData;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.SingleSelect;
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
	private String old_search1;
	private String old_search2;
	private String old_search3;

	private TextField searchField1;
	private TextField searchField2;
	private TextField searchField3;

	private static final long serialVersionUID = 1L;

	private Fnc fnc = new Fnc();

	private Button btn_cancel = new Button("Cancel");
	private Button btn_search = new Button("Search", VaadinIcons.SEARCH);
	private Button btn_ok = new Button("Ok", VaadinIcons.CHECK);
	private Button btn_clr = new Button("Clear");

	private static DecimalFormat df2 = new DecimalFormat(".##");

	private Grid<SearchData> grid = new Grid<>(SearchData.class);
	private GridContextMenu<SearchData> contextMenu;

	public SearchForm(int searchType, TextField searchField1, TextField searchField2, TextField searchField3, CustomerOrderRepository searchRepository) {
		this.searchRepository = searchRepository;
		search1.setValue(searchField1.getValue());
		search2.setValue(searchField2.getValue());
		old_search1 = searchField1.getValue();
		old_search2 = searchField2.getValue();
		this.searchField1 = searchField1;
		this.searchField2 = searchField2;
		if (searchField3 != null) {
			this.searchField3 = searchField3;
			old_search3 = searchField3.getValue();
		}

		this.searchType = searchType;

		initLayout();
		initBehavior();

		btn_ok.setEnabled(false);
		String s1 = search1.getValue() == null ? "" : search1.getValue().trim();
		String s2 = search1.getValue() == null ? "" : search2.getValue().trim();
		switch (searchType) {
		case CUSTOMERS:
			search();
			break;
		case ITEMS:
			DataProvider<SearchData, Void> dp = DataProvider.fromCallbacks(query -> search(query.getOffset(), query.getLimit()).stream(), query -> count());
			grid.setDataProvider(dp);
			break;
		}
	}

	private void initLayout() {

		grid.setColumns("id", "description");

		HeaderRow headerRow = grid.getDefaultHeaderRow();
		headerRow.getCell("id").setComponent(fnc.createFilterField(search1));
		headerRow.getCell("description").setComponent(fnc.createFilterField(search2));
		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_clr, btn_search, btn_ok);
		buttons.setSpacing(true);
		VerticalLayout layout = new VerticalLayout();
		contextMenu = new GridContextMenu<>(grid);
		layout.addComponents(grid, buttons);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior() {

		grid.asSingleSelect().addValueChangeListener(e -> select());
		btn_cancel.addClickListener(e -> {
			searchField1.setValue(old_search1);
			searchField2.setValue(old_search2);
			close();
		});
		btn_search.addClickListener(e -> {

			switch (searchType) {
			case CUSTOMERS:
				search();
				break;
			case ITEMS:
				grid.getDataProvider().refreshAll();
				break;
			}
		});

		btn_ok.addClickListener(e -> close());
		btn_clr.addClickListener(e -> {
			search1.setValue("");
			search2.setValue("");
		});

		contextMenu.addGridHeaderContextMenuListener(event -> {
			contextMenu.removeItems();
			contextMenu.addItem("Search", VaadinIcons.SELECT, selectedMenuItem -> {
				switch (searchType) {
				case CUSTOMERS:
					search();
					break;
				case ITEMS:
					grid.getDataProvider().refreshAll();
					break;
				}
			});
			contextMenu.addItem("Clear", VaadinIcons.SELECT, selectedMenuItem -> {
				search1.setValue("");
				search2.setValue("");
			});
		});

		contextMenu.addGridBodyContextMenuListener(event -> {
			contextMenu.removeItems();
			SingleSelect<SearchData> selected = grid.asSingleSelect();
			SearchData selectedInGrid = selected.getValue();
			if (selectedInGrid != null) {
				contextMenu.addItem("Select", VaadinIcons.SELECT, selectedMenuItem -> {
					if (event.getItem() != null) {
						close();
					}
				});
			}
		});

	}

	private Object select() {
		boolean sel = !grid.asSingleSelect().isEmpty();
		btn_ok.setEnabled(sel);
		if (sel) {
			SingleSelect<SearchData> selected = grid.asSingleSelect();
			search1.setValue(selected.getValue().getId());
			search2.setValue(selected.getValue().getDescription());
			searchField1.setValue(search1.getValue());
			searchField2.setValue(search2.getValue());
			if (searchField3 != null) {

				String str = selected.getValue().getExtra();
				Double dbl = Double.parseDouble(str);
				String formatted = df2.format(dbl);

				searchField3.setValue(formatted);
			}
		}
		return null;
	}

	private Object search() {

		RestResponse<List<SearchData>> resultSet = searchRepository.selectListCustomers(search1.getValue(), search2.getValue());
		if (resultSet.getEntity() != null) {
			grid.setItems(resultSet.getEntity());
		}
		return null;
	}

	private Collection<SearchData> search(int offset, int limit) {
		RestResponse<List<SearchData>> resultSet = searchRepository.promptpaginatesearchItem(offset, limit, search1.getValue(), search2.getValue());
		return resultSet.getEntity();
	}

	private int count() {
		RestResponse<Long> fetched = searchRepository.promptpaginatecount(search1.getValue(), search2.getValue());
		Long numberOfItems = fetched.getEntity();
		return numberOfItems.intValue();
	}

}
