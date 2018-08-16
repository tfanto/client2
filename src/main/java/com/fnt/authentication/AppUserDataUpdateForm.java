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

public class AppUserDataUpdateForm extends Window {

	private static final long serialVersionUID = 6781933231327329883L;
	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Update", VaadinIcons.CHECK);

	private Label login = new Label();
	private Label info = new Label();

	public AppUserDataUpdateForm() {
		initLayout();
		initBehavior();
	}

	private void initLayout() {

		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		buttons.setSpacing(true);

		// HÄR
		GridLayout formLayout = new GridLayout(1, 6, login,  info, buttons);
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




		btn_save.addClickListener(e -> {
			update();
		});

	}

	private void update() {

		if (!checkData()) {
			return;
		}

		RestResponse<Boolean> response = AppUserRepository.updateAppUser();
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

		return false;
	}


}
