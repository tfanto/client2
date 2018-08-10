package com.fnt.customerorder;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fnt.dto.CustomerOrderHeadListView;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.data.provider.DataProvider;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.Composite;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

// https://github.com/melistik/vaadin-grid-util
// https://vaadin.com/directory/component/gridutil
public class CustomerOrderList extends Composite implements View {

	private static final long serialVersionUID = 4797579884478708862L;
	private Fnc fnc = new Fnc();

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	CustomerOrderRepository customerOrderRepository = new CustomerOrderRepository();

	// crud
	private Button btn_refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);
	private Label noOfItems = new Label();

	// filter
	private TextField filterOrderNumber = new TextField();
	private TextField filterCustomerNumber = new TextField();
	private TextField filterName = new TextField();
	private DateField filterDate = new DateField();
	private TextField filterStatus = new TextField();
	private TextField filterChangedBy = new TextField();

	private Grid<CustomerOrderHeadListView> grid = new Grid<>();
	private CustomerOrderHead currentOrderHead = null;

	public CustomerOrderList() {
		initLayout();
		initBehavior();
		// search();
		updateHeader();
	}

	private void initLayout() {

		HorizontalLayout buttons = new HorizontalLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		buttons.setSpacing(false);
		HorizontalLayout header = new HorizontalLayout(buttons, noOfItems);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		// @formatter:off
	       
			grid.addColumn(CustomerOrderHeadListView::getDate)
				.setExpandRatio(0)
				.setId("date");

			grid.addColumn(CustomerOrderHeadListView::getCustomernumber)
			.setExpandRatio(0)
			.setId("customernumber");

			grid.addColumn(CustomerOrderHeadListView::getName)
			.setExpandRatio(0)
			.setId("name");

			grid.addColumn(CustomerOrderHeadListView::getChangedby)
			.setExpandRatio(0)
			.setId("changedby");
			
			grid.addColumn(CustomerOrderHeadListView::getStatus, new NumberRenderer())
			.setExpandRatio(1)
			.setId("status");
			

		HeaderRow row1 = grid.getDefaultHeaderRow();

		fnc.createFilterField(row1,  "customernumber", "Customer no", filterCustomerNumber);
		fnc.createFilterField(row1,  "name", "Name", filterName);
		fnc.createFilterField(row1,  "date", "Date", filterDate);
		fnc.createFilterField(row1,  "status", "Status", filterStatus);
		fnc.createFilterField(row1,  "changedby", "Changedby", filterChangedBy);

		grid.setSizeFull();
		DataProvider<CustomerOrderHeadListView, Void> dp = DataProvider.
				fromFilteringCallbacks(
						query -> search(query.getOffset(), query.getLimit(), fnc.sortInterpretation(query)).stream(), 
						query -> count());
		grid.setDataProvider(dp);

		// @formatter:on

		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();
	}

	private void initBehavior() {

		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		btn_refresh.addClickListener(e -> {
			grid.getDataProvider().refreshAll();
		});
		btn_add.addClickListener(e -> showAddWindow());
		btn_edit.addClickListener(e -> showEditWindow());
		btn_delete.addClickListener(e -> showRemoveWindow());
	}

	public int count() {

		String filterCustomerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String filterNameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		LocalDate filterDateStr = filterDate.getValue();
		String filterStatusStr = filterStatus.getValue() == null ? "" : filterStatus.getValue().trim();
		String filterChangedByStr = filterChangedBy.getValue() == null ? "" : filterChangedBy.getValue().trim();
		RestResponse<Long> fetched = customerOrderRepository.paginatecount(filterCustomerNumberStr, filterNameStr, filterDateStr, filterStatusStr, filterChangedByStr);
		Long numberOfItems = fetched.getEntity();
		noOfItems.setValue("Records : " + numberOfItems);
		return numberOfItems.intValue();
	}

	public Collection<CustomerOrderHeadListView> search(int offset, int limit, Map<String, Boolean> sortingFieldAndDirection) {

		String filterCustomerNumberStr = filterCustomerNumber.getValue() == null ? "" : filterCustomerNumber.getValue().trim();
		String filterNameStr = filterName.getValue() == null ? "" : filterName.getValue().trim();
		LocalDate filterDateStr = filterDate.getValue();
		String filterStatusStr = filterStatus.getValue() == null ? "" : filterStatus.getValue().trim();
		String filterChangedByStr = filterChangedBy.getValue() == null ? "" : filterChangedBy.getValue().trim();
		RestResponse<List<CustomerOrderHeadListView>> fetched = customerOrderRepository.paginatesearch(offset, limit, filterCustomerNumberStr, filterNameStr, filterDateStr, filterStatusStr, filterChangedByStr,
				fnc.map2Str(sortingFieldAndDirection));
		updateHeader();
		return fetched.getEntity();
	}

	public void refreshSearch() {
		grid.getDataProvider().refreshAll();
		grid.scrollToStart();
		grid.deselectAll();
	}

	private void updateHeader() {
		boolean selected = !grid.asSingleSelect().isEmpty();
		btn_edit.setEnabled(selected);
		btn_delete.setEnabled(selected);
	}

	private void showAddWindow() {
		currentOrderHead = null;
		CustomerOrderForm window = new CustomerOrderForm(this, customerOrderRepository, "Add", new CustomerOrderHeadListView(), CRUD_CREATE);
		getUI().addWindow(window);
	}

	private void showEditWindow() {

		SingleSelect<CustomerOrderHeadListView> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<CustomerOrderHead> fetched = customerOrderRepository.getById(id);
		// ensure order is still there
		if (fetched.getStatus().equals(200)) {
			currentOrderHead = fetched.getEntity();
			CustomerOrderForm window = new CustomerOrderForm(this, customerOrderRepository, "Edit", selected.getValue(), CRUD_EDIT); //
			getUI().addWindow(window);
		} else {
			currentOrderHead = null;
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}

	}

	private void showRemoveWindow() {

		SingleSelect<CustomerOrderHeadListView> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		// ensure order is still there
		RestResponse<CustomerOrderHead> fetched = customerOrderRepository.getById(id);
		if (fetched.getStatus().equals(200)) {
			currentOrderHead = fetched.getEntity();
			CustomerOrderForm window = new CustomerOrderForm(this, customerOrderRepository, "Delete", selected.getValue(), CRUD_DELETE);
			getUI().addWindow(window);
		} else {
			currentOrderHead = null;
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	public void setCurrentOrderHead(CustomerOrderHead currentOrderHead) {
		this.currentOrderHead = currentOrderHead;
	}

	public CustomerOrderHead getCurrentOrderHead() {
		return this.currentOrderHead;
	}

}
