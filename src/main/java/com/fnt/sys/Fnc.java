package com.fnt.sys;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

public class Fnc {

	public HorizontalLayout createFilterField(String caption, TextField field, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(field);
		hl.addComponent(chk);
		field.setHeight("95%");
		return hl;
	}

	public HorizontalLayout createFilterField(String caption, DateField field, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(field);
		hl.addComponent(chk);
		field.setHeight("95%");
		return hl;
	}

	public HorizontalLayout createSortField(String caption, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(chk);
		return hl;
	}

	public String formatAppMsg(String appMsg) {
		if (appMsg == null)
			return null;
		if (appMsg.startsWith("["))
			appMsg = appMsg.substring(1);
		if (appMsg.endsWith("]"))
			appMsg = appMsg.substring(0, appMsg.length() - 1);
		String parts[] = appMsg.split(",");
		String ret = "";
		for (int i = 0; i < parts.length; i++) {
			ret += parts[i];
			ret += "\n";
		}
		return ret;
	}

}
