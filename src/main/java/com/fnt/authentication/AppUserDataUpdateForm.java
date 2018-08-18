package com.fnt.authentication;

import com.fnt.entity.AppUser;
import com.fnt.sys.RestResponse;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class AppUserDataUpdateForm extends Window {

	private static final String LOGIN = "login";
	private int crudFunction = 1;
	public static final int CRUD_CREATE = 1;
	public static final int CRUD_EDIT = 2;
	public static final int CRUD_DELETE = 4;

	private static final long serialVersionUID = 6781933231327329883L;
	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Update", VaadinIcons.CHECK);

	private Label info = new Label();

	private Label login = new Label();
	private TextField firstName = new TextField();
	private TextField lastName = new TextField();
	private TextField street = new TextField();
	private TextField ponr = new TextField();
	private TextField padr = new TextField();
	private TextField country = new TextField();
	private TextField phone = new TextField();

	public AppUserDataUpdateForm(int crudFunction) {
		this.crudFunction = crudFunction;
		initLayout();
		initBehavior();
	}

	private void initLayout() {

		Object obj = VaadinSession.getCurrent().getAttribute(LOGIN);
		if (!(obj instanceof String)) {
			close();
		}
		String uid = String.valueOf(obj);
		login.setValue(uid);

		if (crudFunction == CRUD_DELETE) {
			btn_save.setCaption("Delete");
			firstName.setEnabled(false);
			lastName.setEnabled(false);
			street.setEnabled(false);
			ponr.setEnabled(false);
			padr.setEnabled(false);
			country.setEnabled(false);
			phone.setEnabled(false);
		}

		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(info, btn_cancel, btn_save);
		buttons.setSpacing(true);
		login.setCaption("Login");
		firstName.setCaption("Firstname");
		lastName.setCaption("Lastname");
		street.setCaption("Street");
		ponr.setCaption("Postal no");
		padr.setCaption("City");
		country.setCaption("Country");
		phone.setCaption("Phone");

		// HÄR
		HorizontalLayout rad1 = new HorizontalLayout();
		rad1.addComponent(login);

		HorizontalLayout rad2 = new HorizontalLayout();
		rad2.addComponent(firstName);
		rad2.addComponent(lastName);

		HorizontalLayout rad4 = new HorizontalLayout();
		ponr.setWidth("100px");
		rad4.addComponent(ponr);
		padr.setWidth("300px");
		rad4.addComponent(padr);

		GridLayout formLayout = new GridLayout(1, 7, rad1, rad2, street, rad4, country, phone);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		center();
	}

	private void initBehavior() {
		this.setResizable(false);

		Object obj = VaadinSession.getCurrent().getAttribute(LOGIN);
		if (!(obj instanceof String)) {
			close();
		}
		String uid = String.valueOf(obj);

		RestResponse<AppUser> response = AppUserRepository.get(uid);
		if (response.getStatus() != 200) {
			close();
		}

		AppUser fetched = response.getEntity();

		login.setValue(uid);
		firstName.setValue(fetched.getFirstname() == null ? "" : fetched.getFirstname());
		lastName.setValue(fetched.getLastname() == null ? "" : fetched.getLastname());
		street.setValue(fetched.getStreet() == null ? "" : fetched.getStreet());
		ponr.setValue(fetched.getPonr() == null ? "" : fetched.getPonr());
		padr.setValue(fetched.getPadr() == null ? "" : fetched.getPadr());
		country.setValue(fetched.getCountry() == null ? "" : fetched.getCountry());
		phone.setValue(fetched.getPhone() == null ? "" : fetched.getPhone());

		btn_cancel.addClickListener(e -> {
			close();
		});

		btn_save.addClickListener(e -> {
			update();
		});

	}

	private void update() {

		AppUser user = new AppUser();
		user.setLogin(login.getValue());
		user.setFirstname(firstName.getValue());
		user.setLastname(lastName.getValue());
		user.setStreet(street.getValue());
		user.setPonr(ponr.getValue());
		user.setPadr(padr.getValue());
		user.setPhone(phone.getValue());
		user.setCountry(country.getValue());

		RestResponse<Boolean> response = AppUserRepository.updateAppUser(user);
		int ok = response.getStatus();
		if (ok == 200) {
			info.setValue("");
			close();
			return;
		} else {
			info.setValue(response.getMsg());
		}
	}
}
