package com.fnt.authentication;

import com.fnt.sys.RestResponse;
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
	private Label info = new Label();

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

		GridLayout formLayout = new GridLayout(1, 6, login, oldPwd, newPwd, confirmNewPwd, info, buttons);
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
		btn_save.setEnabled(false);

		btn_cancel.addClickListener(e -> {
			close();
		});

		oldPwd.addValueChangeListener(e -> {
			checkData();

		});

		newPwd.addValueChangeListener(e -> {
			String confirmedPwd = confirmNewPwd.getValue();
			String newPwd = e.getValue();
			if (pwdMatches(newPwd, confirmedPwd)) {
				checkData();
			}
		});

		confirmNewPwd.addValueChangeListener(e -> {
			String oldPwd = newPwd.getValue();
			String newPwd = e.getValue();
			if (pwdMatches(oldPwd, newPwd)) {
				checkData();
			}
		});

		btn_save.addClickListener(e -> {
			updatePassword();
		});

	}

	private void updatePassword() {

		if (!checkData()) {
			return;
		}
		String oldPassword = oldPwd.getValue();
		String newPassword = newPwd.getValue();

		RestResponse<Boolean> response = AppUserRepository.updatePassword(oldPassword, newPassword);
		int ok = response.getStatus();
		if (ok == 200) {
			info.setValue("");
			close();
			return;
		} else {
			info.setValue(response.getMsg());
		}
	}

	private Boolean checkData() {

		String oldPassword = oldPwd.getValue().trim();
		if (oldPassword.length() < 1) {
			info.setValue("Old password is empty");
			btn_save.setEnabled(false);
			return false;
		}

		String newPassword = newPwd.getValue().trim();
		if (newPassword.length() > 3) { // a stupid policy but still
			info.setValue("");
			btn_save.setEnabled(true);
			return true;
		}
		btn_save.setEnabled(false);
		info.setValue("Password error");
		return false;
	}

	private Boolean pwdMatches(String oldPwd, String newPwd) {

		if (newPwd.equals(oldPwd)) {
			info.setValue("");
			return true;
		} else {
			btn_save.setEnabled(false);
			info.setValue("Passwords does not match");
			return false;
		}
	}

}
