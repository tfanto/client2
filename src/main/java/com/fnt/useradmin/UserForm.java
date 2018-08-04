package com.fnt.useradmin;

import java.util.List;

import com.fnt.customer.CustomerList;
import com.fnt.dto.UserDto;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class UserForm extends Window {

	private static final long serialVersionUID = 1L;
	int crudFunction = 1;
	UserRepository userRepository;
	UserList owner;

	private TextField login = new TextField("User");

	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Ok", VaadinIcons.CHECK);
	// private Button btn_refresh = new Button("Refresh"); // does not work as
	// expected

	public UserForm(UserList userList, UserRepository userRepository, String string, UserDto user, int crudFunction) {
		this.owner = userList;
		this.userRepository = userRepository;
		this.crudFunction = crudFunction;
		String captionStr = "";
		switch (crudFunction) {
		case CustomerList.CRUD_CREATE:
			captionStr = "Create";
			break;
		case CustomerList.CRUD_EDIT:
			captionStr = "Edit";
			break;
		case CustomerList.CRUD_DELETE:
			captionStr = "Delete";
			btn_save.setCaption("Confirm delete");
			break;
		}

		initLayout(captionStr);
		initBehavior(user);
	}

	private void initLayout(String captionStr) {
		setCaption(captionStr);

		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		// HorizontalLayout buttons = new HorizontalLayout(btn_refresh, btn_cancel,
		// btn_save);
		buttons.setSpacing(true);

		GridLayout formLayout = new GridLayout(1, 1, login);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior(UserDto user) {
		BeanValidationBinder<UserDto> binder = new BeanValidationBinder<>(UserDto.class);
		binder.bindInstanceFields(this);
		binder.readBean(user);

		btn_cancel.addClickListener(e -> close());
		btn_save.addClickListener(e -> {
			try {
				binder.validate();
				binder.writeBean(user);
				RestResponse<UserDto> rs = null;
				switch (crudFunction) {
				case CustomerList.CRUD_CREATE:
					rs = userRepository.create(user);
					break;
				case CustomerList.CRUD_EDIT:
					rs = userRepository.update(user);
					break;
				case CustomerList.CRUD_DELETE:
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
