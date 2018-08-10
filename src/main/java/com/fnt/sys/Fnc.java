package com.fnt.sys;

import java.util.LinkedHashMap;
import java.util.Map;

import com.vaadin.data.provider.Query;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;

public class Fnc {

	public HorizontalLayout createFilterField(TextField field) {
		HorizontalLayout hl = new HorizontalLayout();
		field.addStyleName(ValoTheme.TEXTFIELD_TINY);
		hl.addComponent(field);
		return hl;
	}

	/**/
	public void createFilterField(HeaderRow row1, String columnname, String caption) {
		row1.getCell(columnname).setText(caption);
	}

	/**/
	public void createFilterField(HeaderRow row1, String columnname, String caption, TextField field) {
		field.setPlaceholder(caption);
		field.addStyleName(ValoTheme.TEXTFIELD_TINY);
		row1.getCell(columnname).setComponent(field);
	}

	public void createFilterField(HeaderRow row1, String columnname, String caption, DateField field) {
		field.addStyleName(ValoTheme.DATEFIELD_TINY);
		field.setPlaceholder(caption);
		row1.getCell(columnname).setComponent(field);
	}

	public HorizontalLayout createPrompt(TextField fld, Button btn) {
		HorizontalLayout layout = new HorizontalLayout();
		fld.addStyleName(ValoTheme.TEXTFIELD_TINY);
		btn.addStyleName(ValoTheme.BUTTON_TINY);
		layout.setSpacing(false);
		layout.addComponent(fld);
		layout.addComponent(btn);
		layout.setComponentAlignment(btn, Alignment.BOTTOM_LEFT);
		return layout;
	}

	public HorizontalLayout createPrompt(TextField fld1, TextField fld2, Button btn) {
		HorizontalLayout layout = new HorizontalLayout();
		fld1.addStyleName(ValoTheme.TEXTFIELD_TINY);
		fld2.addStyleName(ValoTheme.TEXTFIELD_TINY);
		btn.addStyleName(ValoTheme.BUTTON_TINY);
		layout.setSpacing(false);
		layout.addComponent(fld1);
		layout.addComponent(fld2);
		layout.addComponent(btn);
		layout.setComponentAlignment(btn, Alignment.BOTTOM_LEFT);
		return layout;
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

	public String getToken(VaadinSession ses) {
		String jwe = null;
		if (ses != null) {
			Object jweObj = ses.getAttribute("jwe");
			if (jweObj instanceof String) {
				jwe = String.valueOf(jweObj);
				return jwe;
			}
		}
		return "";
	}

	public <T, F> Map<String, Boolean> sortInterpretation(Query<T, F> qry) {
		Map<String, Boolean> ret = new LinkedHashMap<>();
		for (QuerySortOrder sortOrder : qry.getSortOrders()) {
			String prop = sortOrder.getSorted();
			Boolean isAscending = SortDirection.ASCENDING.equals(sortOrder.getDirection());
			ret.put(prop, isAscending);
		}
		return ret;
	}

	public String map2Str(Map<String, Boolean> sortingFieldAndDirection) {
		String ret = "";
		for (Map.Entry<String, Boolean> entry : sortingFieldAndDirection.entrySet()) {
			ret += " " + entry.getKey();
			if (entry.getValue()) {
				ret += " ASC ";
			} else {
				ret += " DESC ";
			}
			ret += ",";
		}
		if (ret.length() > 0) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}

}
