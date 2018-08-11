package com.fnt.customer;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fnt.entity.Customer;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.contextmenu.GridContextMenu;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
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

public class CustomerList extends Composite implements View {

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
	private Label noOfItems = new Label();

	// filter
	private TextField filterCustomerNumber = new TextField();
	private TextField filterName = new TextField();

	private Grid<Customer> grid = new Grid<>();
	private GridContextMenu<Customer> contextMenu;

	public CustomerList() {
		initLayout();
		initBehavior();
		updateHeader();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons, noOfItems);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		// @formatter:off
		
		grid.addColumn(Customer::getCustomernumber)
				.setExpandRatio(0)
		        .setId("customernumber");
		
		grid.addColumn(Customer::getName)
				.setExpandRatio(1)
				.setId("name");
		
		HeaderRow row1 = grid.getDefaultHeaderRow();

		grid.setSizeFull();
		fnc.createFilterField(row1, "customernumber", "Customernumber", filterCustomerNumber);
		fnc.createFilterField(row1, "name", "Name", filterName);

		DataProvider<Customer, Void> dp = DataProvider.
				fromFilteringCallbacks(
						query -> search(query.getOffset(), query.getLimit(), fnc.sortInterpretation(query)).stream(), 
						query -> count());
		grid.setDataProvider(dp);
		// @formatter:on
		contextMenu = new GridContextMenu<>(grid);

		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();
	}

	private int count() {
		String customerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String nameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		RestResponse<Long> fetched = customerRepository.paginatecount(customerNumberStr, nameStr);
		Long numberOfItems = fetched.getEntity();
		noOfItems.setValue("Records : " + numberOfItems);
		return numberOfItems.intValue();
	}

	private Collection<Customer> search(int offset, int limit, Map<String, Boolean> sortingFieldAndDirection) {
		String customerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String nameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		RestResponse<List<Customer>> fetched = customerRepository.paginatesearch(offset, limit, customerNumberStr, nameStr, fnc.map2Str(sortingFieldAndDirection));
		updateHeader();
		return fetched.getEntity();
	}

	public void refreshSearch() {
		grid.getDataProvider().refreshAll();
		grid.scrollToStart();
		grid.deselectAll();
	}

	private void initBehavior() {
		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		btn_refresh.addClickListener(e -> {
			grid.getDataProvider().refreshAll();
		});
		btn_add.addClickListener(e -> showAddWindow());
		btn_edit.addClickListener(e -> showEditWindow());
		btn_delete.addClickListener(e -> showRemoveWindow());
		contextMenu.addGridHeaderContextMenuListener(event -> {
			contextMenu.removeItems();
			contextMenu.addItem("Add", VaadinIcons.LIST_OL, selectedMenuItem -> {
				showAddWindow();
			});
		});
		contextMenu.addGridBodyContextMenuListener(event -> {
			contextMenu.removeItems();
			contextMenu.addItem("Add", VaadinIcons.LIST_OL, selectedMenuItem -> {
				showAddWindow();
			});
			contextMenu.addItem("Edit", VaadinIcons.LIST_OL, selectedMenuItem -> {
				if (event.getItem() != null) {
					showEditWindow();
				}
			});
			contextMenu.addItem("Delete", VaadinIcons.LIST_OL, selectedMenuItem -> {
				if (event.getItem() != null) {
					showRemoveWindow();
				}
			});
		});
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
		SingleSelect<Customer> selected = grid.asSingleSelect();
		Customer selectedInGrid = selected.getValue();
		if (selectedInGrid != null) {
			Long id = selectedInGrid.getId();
			// get from the server, it could have been removed
			RestResponse<Customer> fetched = customerRepository.getById(id);
			if (fetched.getStatus().equals(200)) {
				Customer obj = fetched.getEntity();
				CustomerForm window = new CustomerForm(this, customerRepository, "Edit", obj, CRUD_EDIT);
				getUI().addWindow(window);
			} else {
				Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
			}
		}
	}

	private void showRemoveWindow() {
		SingleSelect<Customer> selected = grid.asSingleSelect();
		Customer selectedInGrid = selected.getValue();
		if (selectedInGrid != null) {
			CustomerForm window = new CustomerForm(this, customerRepository, "Delete", grid.asSingleSelect().getValue(), CRUD_DELETE);
			getUI().addWindow(window);
		}
	}
}
