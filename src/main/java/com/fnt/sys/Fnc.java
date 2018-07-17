package com.fnt.sys;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;

public class Fnc {

	public void createFilterField(HeaderRow row1, HeaderRow row2, HeaderRow row3, String columnname, String caption, TextField field, CheckBox chk) {
		Label lbl = new Label(caption);
		row1.getCell(columnname).setComponent(lbl);
		field.setHeight("90%");
		row2.getCell(columnname).setComponent(field);
		row3.getCell(columnname).setComponent(chk);
	}

	public void createFilterField(HeaderRow row1, HeaderRow row2, HeaderRow row3, String columnname, String caption, DateField field, CheckBox chk) {
		row1.getCell(columnname).setComponent(new Label(caption));
		field.setHeight("90%");
		row2.getCell(columnname).setComponent(field);
		row3.getCell(columnname).setComponent(chk);
	}

	public HorizontalLayout createFilterField(String caption, TextField field, CheckBox chk) {
		HorizontalLayout hl = new HorizontalLayout();
		hl.addComponent(new Label(caption));
		hl.addComponent(field);
		hl.addComponent(chk);
		field.setHeight("97%");
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

	public VerticalLayout createLineFragment(String caption, Component component) {
		VerticalLayout vertical = new VerticalLayout();
		HorizontalLayout captionLayout = new HorizontalLayout();
		HorizontalLayout dataLayout = new HorizontalLayout();
		captionLayout.setSpacing(false);
		dataLayout.setSpacing(false);
		vertical.addComponent(captionLayout);
		vertical.addComponent(dataLayout);
		Label label = new Label(caption);
		captionLayout.addComponent(label);
		dataLayout.addComponent(component);
		return vertical;
	}


}
