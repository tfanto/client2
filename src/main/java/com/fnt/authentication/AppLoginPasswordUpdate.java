package com.fnt.authentication;

import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class AppLoginPasswordUpdate extends Window {

	private static final long serialVersionUID = 6781933231327329883L;
	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Update", VaadinIcons.CHECK);

	private Label login = new Label();
	private PasswordField oldPwd = new PasswordField();
	private PasswordField newPwd = new PasswordField();
	private PasswordField confirmNewPwd = new PasswordField();
	private Label showIfNewPwdMatches = new Label();


	public AppLoginPasswordUpdate() {
		initLayout();
		initBehavior();
	}

	private void initLayout() {

		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);
		oldPwd.setPlaceholder("Old password");
		newPwd.setPlaceholder("New password");
		confirmNewPwd.setPlaceholder("Confirm password");

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		buttons.setSpacing(true);

		GridLayout formLayout = new GridLayout(1, 6, login, oldPwd, newPwd, confirmNewPwd, showIfNewPwdMatches, buttons);
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
		
		btn_cancel.addClickListener(e ->{
			close();
		});
	}

}
