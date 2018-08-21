package com.fnt.item;

import java.text.NumberFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.fnt.entity.Customer;
import com.fnt.entity.Item;
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
import com.vaadin.ui.renderers.NumberRenderer;
import com.vaadin.ui.themes.ValoTheme;

public class ItemList extends Composite implements View {

	private Fnc fnc = new Fnc();

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
	private Label noOfItems = new Label();
	// filter
	private TextField filterItemNumber = new TextField();
	private TextField filterDescription = new TextField();
	private TextField filterOrderingPoint = new TextField();
	private TextField filterInStock = new TextField();
	private TextField filterPrice = new TextField();
	private TextField filterPurchasePrice = new TextField();

	private Grid<Item> grid = new Grid<>();
	private GridContextMenu<Item> contextMenu;

	public ItemList() {
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
	       
			grid.addColumn(Item::getItemnumber)
				.setExpandRatio(0)
				.setId("itemnumber");

			grid.addColumn(Item::getDescription)
			.setExpandRatio(0)
			.setId("description");
			
			grid.addColumn(Item::getOrderingpoint)
			.setExpandRatio(0)
			.setId("orderingpoint");
			
			grid.addColumn(Item::getInstock)
			.setExpandRatio(0)
			.setId("instock");

			grid.addColumn(Item::getPrice, new NumberRenderer(NumberFormat.getCurrencyInstance()))
			.setExpandRatio(0)
			.setId("price");
			
			grid.addColumn(Item::getPurchaseprice, new NumberRenderer(NumberFormat.getCurrencyInstance()))
			.setExpandRatio(1)
			.setId("purchaseprice");
		
		HeaderRow row1 = grid.getDefaultHeaderRow();

		fnc.createFilterField(row1, "itemnumber", "Itemnumber", filterItemNumber);
		fnc.createFilterField(row1, "description", "Descr", filterDescription);
		fnc.createFilterField(row1, "orderingpoint", "Order point");
		fnc.createFilterField(row1, "instock", "In stock");
		fnc.createFilterField(row1, "price", "Price");
		fnc.createFilterField(row1, "purchaseprice", "Purchase price");

		grid.setSizeFull();
		DataProvider<Item, Void> dp = DataProvider.
				fromFilteringCallbacks(
						query -> search(query.getOffset(), query.getLimit(), fnc.sortInterpretation(query)).stream(), 
						query -> count());
		grid.setDataProvider(dp);
		contextMenu = new GridContextMenu<>(grid);
		// @formatter:on

		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();
	}

	private int count() {
		String itemNumberStr = filterItemNumber.getValue() == null ? "" : filterItemNumber.getValue().trim();
		String descriptionStr = filterDescription.getValue() == null ? "" : filterDescription.getValue().trim();
		RestResponse<Long> fetched = itemRepository.paginatecount(itemNumberStr, descriptionStr);
		Long numberOfItems = fetched.getEntity();
		noOfItems.setValue("Records : " + numberOfItems);
		return numberOfItems.intValue();
	}

	private Collection<Item> search(int offset, int limit, Map<String, Boolean> sortingFieldAndDirection) {
		String itemNumberStr = filterItemNumber.getValue() == null ? "" : filterItemNumber.getValue().trim();
		String descriptionStr = filterDescription.getValue() == null ? "" : filterDescription.getValue().trim();
		RestResponse<List<Item>> fetched = itemRepository.paginatesearch(offset, limit, itemNumberStr, descriptionStr, fnc.map2Str(sortingFieldAndDirection));
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
			contextMenu.addItem("Add", VaadinIcons.PLUS, selectedMenuItem -> {
				showAddWindow();
			});
		});
		contextMenu.addGridBodyContextMenuListener(event -> {
			contextMenu.removeItems();
			contextMenu.addItem("Add", VaadinIcons.PLUS, selectedMenuItem -> {
				showAddWindow();
			});

			SingleSelect<Item> selected = grid.asSingleSelect();
			Item selectedInGrid = selected.getValue();
			if (selectedInGrid != null) {

				contextMenu.addItem("Edit", VaadinIcons.PENCIL, selectedMenuItem -> {
					if (event.getItem() != null) {
						showEditWindow();
					}
				});
				contextMenu.addItem("Delete", VaadinIcons.TRASH, selectedMenuItem -> {
					if (event.getItem() != null) {
						showRemoveWindow();
					}
				});
			}
		});
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
		Item selectedInGrid = selected.getValue();
		if (selectedInGrid != null) {
			Long id = selected.getValue().getId();
			RestResponse<Item> fetched = itemRepository.getById(id);
			if (fetched.getStatus().equals(200)) {
				Item obj = fetched.getEntity();
				ItemForm window = new ItemForm(this, itemRepository, "Edit", obj, CRUD_EDIT);
				getUI().addWindow(window);
			} else {
				Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
				if (fetched.getStatus().equals(404)) {
					grid.getDataProvider().refreshAll();
					grid.deselectAll();					
				}
			}
		}
	}

	private void showRemoveWindow() {
		SingleSelect<Item> selected = grid.asSingleSelect();
		Item selectedInGrid = selected.getValue();
		if (selectedInGrid != null) {
			Long id = selected.getValue().getId();
			RestResponse<Item> fetched = itemRepository.getById(id);
			if (fetched.getStatus().equals(200)) {
				Item obj = fetched.getEntity();
				ItemForm window = new ItemForm(this, itemRepository, "Edit", obj, CRUD_DELETE);
				getUI().addWindow(window);
			} else {
				Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
				if (fetched.getStatus().equals(404)) {
					grid.getDataProvider().refreshAll();
					grid.deselectAll();					
				}				
			}
		}
	}

}
