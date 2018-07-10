package com.fnt.user;

import java.util.ArrayList;
import java.util.List;
import com.fnt.role.RoleRepository;
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

public class UserList extends Composite {

	private static final long serialVersionUID = 4797579884478708862L;

	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	UserRepository userRepository = new UserRepository();
	RoleRepository roleRepository = new RoleRepository();

	// crud
	private Button refresh = new Button("", VaadinIcons.SEARCH);
	private Button add = new Button("", VaadinIcons.PLUS);
	private Button edit = new Button("", VaadinIcons.PENCIL);
	private Button delete = new Button("", VaadinIcons.TRASH);
	// filter
	private TextField filterFirstName = new TextField();
	private TextField filterLastName = new TextField();
	private TextField filterEmail = new TextField();
	// sorting
	private Label filterSortOrder = new Label();
	private List<String> selectedSort = new ArrayList<>();
	private CheckBox sortFirstname = new CheckBox();
	private CheckBox sortLastname = new CheckBox();
	private CheckBox sortEmail = new CheckBox();

	private Grid<User> grid = new Grid<>(User.class);

	public UserList() {
		initLayout();
		initBehavior();
		search();
	}

	private HorizontalLayout createFilterField(String caption, TextField field, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(field);
		hl.addComponent(chk);
		field.setHeight("95%");
		return hl;
	}

	private void initLayout() {

		CssLayout buttons = new CssLayout(add, edit, delete, refresh);
		HorizontalLayout header = new HorizontalLayout(buttons, filterSortOrder);
		header.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
		header.setSpacing(true);

		grid.setColumns("firstname", "lastname", "email", "mainrole");

		HeaderRow headerRow = grid.getDefaultHeaderRow();
		headerRow.getCell("firstname").setComponent(createFilterField("Firstname", filterFirstName, sortFirstname));
		headerRow.getCell("lastname").setComponent(createFilterField("Lastname", filterLastName, sortLastname));
		headerRow.getCell("email").setComponent(createFilterField("Email", filterEmail, sortEmail));

		grid.setSizeFull();

		for (@SuppressWarnings("rawtypes") Grid.Column column : grid.getColumns()) {
			column.setSortable(false);
		}
		VerticalLayout layout = new VerticalLayout(header, grid);
		layout.setExpandRatio(grid, 1);
		setCompositionRoot(layout);
		setSizeFull();
	}

	private void initBehavior() {
		grid.asSingleSelect().addValueChangeListener(e -> updateHeader());
		refresh.addClickListener(e -> search());
		add.addClickListener(e -> showAddWindow());
		edit.addClickListener(e -> showEditWindow());
		delete.addClickListener(e -> showRemoveWindow());

		sortFirstname.addValueChangeListener(e -> evaluateFirstnameSort());
		sortLastname.addValueChangeListener(e -> evaluateLastnameSort());
		sortEmail.addValueChangeListener(e -> evaluateEmailSort());
		showSort();
	}

	private Object evaluateFirstnameSort() {
		Boolean val = sortFirstname.getValue();
		if (val) {
			selectedSort.add("Firstname");
		} else {
			selectedSort.remove("Firstname");
		}
		showSort();
		return null;
	}


	private Object evaluateLastnameSort() {
		Boolean val = sortLastname.getValue();
		if (val) {
			selectedSort.add("Lastname");
		} else {
			selectedSort.remove("Lastname");
		}
		showSort();
		return null;
	}

	private Object evaluateEmailSort() {
		Boolean val = sortEmail.getValue();
		if (val) {
			selectedSort.add("Email");
		} else {
			selectedSort.remove("Email");
		}
		showSort();
		return null;
	}
	
	private void showSort() {
		
		if(selectedSort.size() < 1) {
			sortFirstname.setValue(true);
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

		String firstNameStr = filterFirstName.getValue() == null ? "" : filterFirstName.getValue().trim();
		String lastNameStr = filterLastName.getValue() == null ? "" : filterLastName.getValue().trim();
		String emailStr = filterEmail.getValue() == null ? "" : filterEmail.getValue().trim();
		String sortOrder = filterSortOrder.getValue();

		RestResponse<List<User>> rs = userRepository.search(firstNameStr, lastNameStr, emailStr, sortOrder);
		grid.setItems(rs.getEntity());
		updateHeader();
	}

	private void updateHeader() {
		boolean selected = !grid.asSingleSelect().isEmpty();
		edit.setEnabled(selected);
		delete.setEnabled(selected);
	}

	private void showAddWindow() {
		UserForm window = new UserForm(this, userRepository, roleRepository, "Add", new User(), CRUD_CREATE);
		getUI().addWindow(window);
	}

	private void showEditWindow() {
		// get from the server, it could have been removed
		SingleSelect<User> selected = grid.asSingleSelect();
		Long id = selected.getValue().getId();
		RestResponse<User> fetched = userRepository.getById(id);

		if (fetched.getStatus().equals(200)) {
			User fetchedUser = fetched.getEntity();
			UserForm window = new UserForm(this, userRepository, roleRepository, "Edit", fetchedUser, CRUD_EDIT);
			getUI().addWindow(window);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void showRemoveWindow() {
		UserForm window = new UserForm(this, userRepository, roleRepository, "Delete", grid.asSingleSelect().getValue(),
				CRUD_DELETE);
		getUI().addWindow(window);
	}
}
