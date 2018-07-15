package com.fnt.customerorder;

import java.util.ArrayList;
import java.util.List;

import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerOrderList extends Composite {

	private static final long serialVersionUID = 4797579884478708862L;
	private Fnc fnc = new Fnc();

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	CustomerOrderRepository itemRepository = new CustomerOrderRepository();

	// crud
	private Button btn_refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);
	// filter
	private TextField filterOrderNumber = new TextField();
	private TextField filterCustomerNumber = new TextField();
	private TextField filterName = new TextField();
	private TextField filterDate = new TextField();
	private TextField filterStatus = new TextField();
	private TextField filterChangedBy = new TextField();

	// sorting
	private Label filterSortOrder = new Label();
	private List<String> selectedSort = new ArrayList<>();

	private CheckBox sortOrderNumber = new CheckBox();
	private CheckBox sortCustomerNumber = new CheckBox();
	private CheckBox sortName = new CheckBox();
	private CheckBox sortDate = new CheckBox();
	private CheckBox sortStatus = new CheckBox();
	private CheckBox sortChangedBy = new CheckBox();

	private Grid<CustomerOrderHeadListView> grid = new Grid(CustomerOrderHeadListView.class);

	public CustomerOrderList() {
		initLayout();
		initBehavior();
		// search();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons, filterSortOrder);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		grid.setColumns("customernumber", "name", "date", "changedby", "status");

		HeaderRow headerRow = grid.getDefaultHeaderRow();

		headerRow.getCell("customernumber").setComponent(fnc.createFilterField("Customernumber", filterCustomerNumber, sortCustomerNumber));
		headerRow.getCell("name").setComponent(fnc.createFilterField("Name", filterName, sortName));
		headerRow.getCell("date").setComponent(fnc.createFilterField("Date", filterDate, sortDate));
		headerRow.getCell("status").setComponent(fnc.createFilterField("Status", filterStatus, sortStatus));
		headerRow.getCell("changedby").setComponent(fnc.createFilterField("Changedby", filterChangedBy, sortChangedBy));

		grid.setSizeFull();
		
		for (@SuppressWarnings("rawtypes")
		Grid.Column column : grid.getColumns()) {
			column.setSortable(false);
		}
		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();
	}

	private void initBehavior() {

		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		btn_refresh.addClickListener(e -> search());
		btn_add.addClickListener(e -> showAddWindow());
		btn_edit.addClickListener(e -> showEditWindow());
		btn_delete.addClickListener(e -> showRemoveWindow());

		sortCustomerNumber.addValueChangeListener(e -> evaluateSort(sortCustomerNumber, "CustomerNumber"));
		sortName.addValueChangeListener(e -> evaluateSort(sortName, "Name"));
		sortDate.addValueChangeListener(e -> evaluateSort(sortDate, "Date"));
		sortStatus.addValueChangeListener(e -> evaluateSort(sortStatus, "Status"));
		sortChangedBy.addValueChangeListener(e -> evaluateSort(sortChangedBy, "ChangedBy"));

		showSort();
	}

	private Object evaluateSort(CheckBox checkBox, String fieldName) {
		Boolean val = checkBox.getValue();
		if (val) {
			selectedSort.add(fieldName);
		} else {
			selectedSort.remove(fieldName);
		}
		showSort();
		return null;
	}
	
	private void showSort() {

		String theSort = "";
		for (String dta : selectedSort) {
			theSort += dta;
			theSort += ",";
		}
		if (theSort.endsWith(",")) {
			theSort = theSort.substring(0, theSort.length() - 1);
		}
		filterSortOrder.setValue(theSort);
	}

	public void search() {

		String filterCustomerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String filterNameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		String filterDateStr = filterDate.getValue() == null ? "" : filterDate.getValue().trim();
		String filterStatusStr = filterStatus.getValue() == null ? "" : filterStatus.getValue().trim();
		String filterChangedByStr = filterChangedBy.getValue() == null ? "" : filterChangedBy.getValue().trim();

		String sortOrder = filterSortOrder.getValue();

		RestResponse<List<CustomerOrderHeadListView>> fetched = itemRepository.search(filterCustomerNumberStr, filterNameStr, filterDateStr, filterStatusStr, filterChangedByStr, sortOrder);
		if (fetched.getStatus().equals(200)) {
			grid.setItems(fetched.getEntity());
			updateHeader();
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void updateHeader() {
		boolean selected = !grid.asSingleSelect().isEmpty();
		btn_edit.setEnabled(selected);
		btn_delete.setEnabled(selected);
	}

	private void showAddWindow() {
		// ItemForm window = new ItemForm(this, itemRepository, "Add", new Item(),
		// CRUD_CREATE);
		// getUI().addWindow(window);
	}

	private void showEditWindow() {
		// get from the server, it could have been removed
		/*
		SingleSelect<CustomerOrderHeadListView> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<CustomerOrderHead> fetched = itemRepository.getById(id);

		if (fetched.getStatus().equals(200)) {
			CustomerOrderHeadListView obj = fetched.getEntity();
			// ItemForm window = new ItemForm(this, itemRepository, "Edit", obj, CRUD_EDIT);
			// getUI().addWindow(window);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
		*/
	}

	private void showRemoveWindow() {
		// ItemForm window = new ItemForm(this, itemRepository, "Delete",
		// grid.asSingleSelect().getValue(), CRUD_DELETE);
		// getUI().addWindow(window);
	}

}