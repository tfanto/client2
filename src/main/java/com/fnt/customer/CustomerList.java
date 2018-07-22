package com.fnt.customer;

import java.util.ArrayList;
import java.util.List;

import com.fnt.entity.Customer;
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

public class CustomerList extends Composite {

	private Fnc fnc = new Fnc();

	private static final long serialVersionUID = 4797579884478708862L;

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	CustomerRepository customerRepository = new CustomerRepository();

	// crud
	private Button btn_refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);
	// filter
	private TextField filterCustomerNumber = new TextField();
	private TextField filterName = new TextField();
	// sorting
	private Label filterSortOrder = new Label();
	private List<String> selectedSort = new ArrayList<>();
	private CheckBox sortCustomerNumber = new CheckBox();
	private CheckBox sortName = new CheckBox();

	private Grid<Customer> grid = new Grid<>(Customer.class);

	public CustomerList() {
		initLayout();
		initBehavior();
		search();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons, filterSortOrder);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		grid.setColumns("customernumber", "name");
		
		HeaderRow row1 = grid.getDefaultHeaderRow();
		HeaderRow row2 = grid.addHeaderRowAt(grid.getHeaderRowCount());
		HeaderRow row3 = grid.addHeaderRowAt(grid.getHeaderRowCount());

		fnc.createFilterField(row1, row2, row3, "customernumber", "Customernumber", filterCustomerNumber, sortCustomerNumber);
		fnc.createFilterField(row1, row2, row3, "name", "Name", filterName, sortName);

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

		sortCustomerNumber.addValueChangeListener(e -> evaluateCustomerNumberSort());
		sortName.addValueChangeListener(e -> evaluateNameSort());
		showSort();
	}

	private Object evaluateNameSort() {
		Boolean val = sortName.getValue();
		if (val) {
			selectedSort.add("Name");
		} else {
			selectedSort.remove("Name");
		}
		showSort();
		return null;
	}

	private Object evaluateCustomerNumberSort() {
		Boolean val = sortCustomerNumber.getValue();
		if (val) {
			selectedSort.add("CustomerNumber");
		} else {
			selectedSort.remove("CustomerNumber");
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

		String customerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String nameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		String sortOrder = filterSortOrder.getValue();

		RestResponse<List<Customer>> rs = customerRepository.search(customerNumberStr, nameStr, sortOrder);
		grid.setItems(rs.getEntity());
		updateHeader();
	}

	private void updateHeader() {
		boolean selected = !grid.asSingleSelect().isEmpty();
		btn_edit.setEnabled(selected);
		btn_delete.setEnabled(selected);
	}

	private void showAddWindow() {
		CustomerForm window = new CustomerForm(this, customerRepository, "Add", new Customer(), CRUD_CREATE);
		getUI().addWindow(window);
	}

	private void showEditWindow() {
		// get from the server, it could have been removed
		SingleSelect<Customer> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<Customer> fetched = customerRepository.getById(id);

		if (fetched.getStatus().equals(200)) {
			Customer obj = fetched.getEntity();
			CustomerForm window = new CustomerForm(this, customerRepository, "Edit", obj, CRUD_EDIT);
			getUI().addWindow(window);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void showRemoveWindow() {
		CustomerForm window = new CustomerForm(this, customerRepository, "Delete", grid.asSingleSelect().getValue(), CRUD_DELETE);
		getUI().addWindow(window);
	}
}
