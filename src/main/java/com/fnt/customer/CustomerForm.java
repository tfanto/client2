package com.fnt.customer;

import java.util.List;

import com.fnt.entity.Customer;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerForm extends Window {

	private static final long serialVersionUID = -1559073890684080775L;

	int crudFunction = 1;
	CustomerRepository customerRepository;
	CustomerList owner;

	private TextField firstName = new TextField("First name");
	private TextField lastName = new TextField("Last name");
	private TextField email = new TextField("Email");
	private PasswordField password = new PasswordField("Password");
	private CheckBox blocked = new CheckBox("Blocked");

	private Button cancel = new Button("Cancel");
	private Button save = new Button("Ok", VaadinIcons.CHECK);
	// private Button refresh = new Button("Refresh"); // does not work as expected

	public CustomerForm(CustomerList owner, CustomerRepository customerRepository, String caption, Customer user,
			int crudFunction) {
		this.owner = owner;
		this.customerRepository = customerRepository;

		this.crudFunction = crudFunction;

		switch (crudFunction) {
		case CustomerList.CRUD_DELETE:
			save.setCaption("Confirm delete");
			break;
		}

		initLayout(caption);
		initBehavior(user);
	}

	private void fetchCustomer(Long id) {

		RestResponse<Customer> fetched = customerRepository.getById(id);
		if (fetched.getStatus().equals(200)) {
			Customer fetchedUser = fetched.getEntity();
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

		GridLayout formLayout = new GridLayout(3, 3, firstName, lastName, email, password, blocked);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior(Customer customer) {
		BeanValidationBinder<Customer> binder = new BeanValidationBinder<>(Customer.class);
		binder.bindInstanceFields(this);
		binder.readBean(customer);

		// refresh.addClickListener(e -> fetchUser(user.getId()));

		cancel.addClickListener(e -> close());
		save.addClickListener(e -> {
			try {
				binder.validate();
				binder.writeBean(customer);
				RestResponse<Customer> rs = null;
				switch (crudFunction) {
				case CustomerList.CRUD_CREATE:
					rs = customerRepository.create(customer);
					break;
				case CustomerList.CRUD_EDIT:
					rs = customerRepository.update(customer);
					break;
				case CustomerList.CRUD_DELETE:
					rs = customerRepository.delete(customer);
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
				List<BindingValidationStatus<?>> errors = ex.getFieldValidationErrors();
				String msg = "";
				for (BindingValidationStatus<?> error : errors) {
					msg += error.getResult().get().getErrorMessage();
					// TODO close but no cigar where are the field names ???
					msg += "\n";
				}
				Notification.show("ERROR", msg, Notification.Type.ERROR_MESSAGE);
			}
		});
	}
}
