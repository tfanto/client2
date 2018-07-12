package com.fnt.item;

import java.util.ArrayList;
import java.util.List;

import com.fnt.entity.Item;
import com.fnt.sys.RestResponse;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.SingleSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

public class ItemList extends Composite {

	private static final long serialVersionUID = 4797579884478708862L;

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	ItemRepository itemRepository = new ItemRepository();

	// crud
	private Button btn_refresh = new Button("", VaadinIcons.SEARCH);
	private Button btn_add = new Button("", VaadinIcons.PLUS);
	private Button btn_edit = new Button("", VaadinIcons.PENCIL);
	private Button btn_delete = new Button("", VaadinIcons.TRASH);
	// filter
	private TextField filterItemNumber = new TextField();
	private TextField filterDescription = new TextField();
	private TextField filterOrderingPoint = new TextField();
	private TextField filterInStock = new TextField();
	private TextField filterPrice = new TextField();
	private TextField filterPurchasePrice = new TextField();
	// sorting
	private Label filterSortOrder = new Label();
	private List<String> selectedSort = new ArrayList<>();
	private CheckBox sortItemNumber = new CheckBox();
	private CheckBox sortDescription = new CheckBox();
	private CheckBox sortOrderingPoint = new CheckBox();
	private CheckBox sortInStock = new CheckBox();
	private CheckBox sortPrice = new CheckBox();
	private CheckBox sortPurchasePrice = new CheckBox();

	private Grid<Item> grid = new Grid<>(Item.class);

	public ItemList() {
		initLayout();
		initBehavior();
		// search();
	}

	private HorizontalLayout createFilterField(String caption, TextField field, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(field);
		hl.addComponent(chk);
		field.setHeight("95%");
		return hl;
	}

	private HorizontalLayout createSortField(String caption, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(chk);
		return hl;
	}

	private void initLayout() {

		CssLayout buttons = new CssLayout(btn_add, btn_edit, btn_delete, btn_refresh);
		HorizontalLayout header = new HorizontalLayout(buttons, filterSortOrder);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		grid.setColumns("itemnumber", "description", "orderingpoint", "instock", "price", "purchaseprice");

		HeaderRow headerRow = grid.getDefaultHeaderRow();
		headerRow.getCell("itemnumber")
				.setComponent(createFilterField("Item number", filterItemNumber, sortItemNumber));

		headerRow.getCell("description")
				.setComponent(createFilterField("Description", filterDescription, sortDescription));

		headerRow.getCell("orderingpoint").setComponent(createSortField("OrderingPoint", sortOrderingPoint));
		headerRow.getCell("instock").setComponent(createSortField("In Stock", sortInStock));
		headerRow.getCell("price").setComponent(createSortField("Price", sortPrice));
		headerRow.getCell("purchaseprice").setComponent(createSortField("Purchase price", sortPurchasePrice));

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

		sortItemNumber.addValueChangeListener(e -> evaluateSort(sortItemNumber, "ItemNumber"));
		sortDescription.addValueChangeListener(e -> evaluateSort(sortDescription, "Description"));
		sortOrderingPoint.addValueChangeListener(e -> evaluateSort(sortOrderingPoint, "OrderingPoint"));
		sortInStock.addValueChangeListener(e -> evaluateSort(sortInStock, "InStock"));
		sortPrice.addValueChangeListener(e -> evaluateSort(sortPrice, "Price"));
		sortPurchasePrice.addValueChangeListener(e -> evaluateSort(sortPurchasePrice, "PurchasePrice"));
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

		// default
		if (selectedSort.size() < 1) {
			sortItemNumber.setValue(true);
		}

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

		String itemNumberStr = filterItemNumber.getValue() == null ? "" : filterItemNumber.getValue().trim();
		String descriptionStr = filterDescription.getValue() == null ? "" : filterDescription.getValue().trim();

		// String orderingPointStr = filterOrderingPoint.getValue() == null ? "" :
		// filterOrderingPoint.getValue().trim();
		// String inStockStr = filterInStock.getValue() == null ? "" :
		// filterInStock.getValue().trim();

		// String priceStr = filterPrice.getValue() == null ? "" :
		// filterPrice.getValue().trim();
		// String purchasePriceStr = filterPurchasePrice.getValue() == null ? "" :
		// filterPurchasePrice.getValue().trim();

		String sortOrder = filterSortOrder.getValue();

		RestResponse<List<Item>> fetched = itemRepository.search(itemNumberStr, descriptionStr, sortOrder);
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
		ItemForm window = new ItemForm(this, itemRepository, "Add", new Item(), CRUD_CREATE);
		getUI().addWindow(window);
	}

	private void showEditWindow() {
		// get from the server, it could have been removed
		SingleSelect<Item> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<Item> fetched = itemRepository.getById(id);

		if (fetched.getStatus().equals(200)) {
			Item obj = fetched.getEntity();
			ItemForm window = new ItemForm(this, itemRepository, "Edit", obj, CRUD_EDIT);
			getUI().addWindow(window);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void showRemoveWindow() {
		ItemForm window = new ItemForm(this, itemRepository, "Delete", grid.asSingleSelect().getValue(), CRUD_DELETE);
		getUI().addWindow(window);
	}

}
