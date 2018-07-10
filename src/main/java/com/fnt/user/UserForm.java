package com.fnt.user;

import com.fnt.role.Role;
import com.fnt.role.RoleRepository;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CheckBoxGroup;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class UserForm extends Window {

	private static final long serialVersionUID = -1559073890684080775L;

	int crudFunction = 1;
	UserRepository userRepository;
	RoleRepository roleRepository;
	UserList owner;

	private TextField firstName = new TextField("First name");
	private TextField lastName = new TextField("Last name");
	private TextField email = new TextField("Email");
	private PasswordField password = new PasswordField("Password");
	private CheckBoxGroup<Role> roles;
	private ComboBox<Role> mainRole;
	private CheckBox blocked = new CheckBox("Blocked");

	private Button cancel = new Button("Cancel");
	private Button save = new Button("Ok", VaadinIcons.CHECK);
	//private Button refresh = new Button("Refresh");  // does not work as expected

	public UserForm(UserList owner, UserRepository userRepository, RoleRepository roleRepository, String caption,
			User user, int crudFunction) {
		this.owner = owner;
		this.userRepository = new UserRepository();
		this.roleRepository = new RoleRepository();

		roles = new CheckBoxGroup<>("Roles", roleRepository.findAll());
		mainRole = new ComboBox<>("Main Role", roleRepository.findAll());

		this.crudFunction = crudFunction;

		switch (crudFunction) {
		case UserList.CRUD_DELETE:
			save.setCaption("Confirm delete");
			break;
		}

		initLayout(caption);
		initBehavior(user);
	}

	private void fetchUser(Long id) {

		RestResponse<User> fetched = userRepository.getById(id);
		if (fetched.getStatus().equals(200)) {
			User fetchedUser = fetched.getEntity();
			initBehavior(fetchedUser);
		} else {
			Notification.show("ERROR", fetched.getMsg(), Notification.Type.ERROR_MESSAGE);
		}
	}

	private void initLayout(String caption) {
		setCaption(caption);
		save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(cancel, save);
		// HorizontalLayout buttons = new HorizontalLayout(refresh, cancel, save);
		buttons.setSpacing(true);

		GridLayout formLayout = new GridLayout(3, 3, firstName, lastName, email, password, roles, mainRole, blocked);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior(User user) {
		BeanValidationBinder<User> binder = new BeanValidationBinder<>(User.class);
		binder.bindInstanceFields(this);
		binder.readBean(user);

		// refresh.addClickListener(e -> fetchUser(user.getId()));

		cancel.addClickListener(e -> close());
		save.addClickListener(e -> {
			try {
				binder.validate();
				binder.writeBean(user);
				RestResponse<User> rs = null;
				switch (crudFunction) {
				case UserList.CRUD_CREATE:
					rs = userRepository.create(user);
					break;
				case UserList.CRUD_EDIT:
					rs = userRepository.update(user);
					break;
				case UserList.CRUD_DELETE:
					rs = userRepository.delete(user);
					break;
				default: {
					return;
				}
				}
				if (!rs.getStatus().equals(200)) {
					Notification.show("ERROR", rs.getMsg(), Notification.Type.ERROR_MESSAGE);
				} else {
					close();
					owner.search();
				}
			} catch (ValidationException ex) {
				Notification.show("Please fix the errors and try again");
			}
		});
	}
}
