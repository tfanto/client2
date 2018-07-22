package com.fnt.customerorder;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;

import com.fnt.customer.CustomerRepository;
import com.fnt.dto.CustomerOrderLineListView;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.Item;
import com.fnt.item.ItemRepository;
import com.fnt.search.SearchForm;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.Binder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationException;
import com.vaadin.data.converter.StringToDoubleConverter;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerOrderForm extends Window {

	private Fnc fnc = new Fnc();

	NumberFormat numberFormat = new DecimalFormat("#,###.00");

	private Grid<CustomerOrderLineListView> grid = new Grid<>();

	private static final long serialVersionUID = -214415727456373593L;
	int crudFunction = 1;
	CustomerOrderRepository customerOrderRepository = new CustomerOrderRepository();
	CustomerOrderList owner;

	// headerInfo
	private DateField orderdate = new DateField("Date");
	private TextField customernumber = new TextField("Customer no");
	private Button btn_customernumber = new Button("...");
	private TextField name = new TextField("Name");
	// lineInfo

	private TextField itemnumber = new TextField("Item no");
	private Button btn_itemnumber = new Button("...");
	private TextField itemdescription = new TextField("Description");
	private TextField units = new TextField("Units");
	private TextField priceperitem = new TextField("Price per Item");
	private Button btn_clearline = new Button("Clear");
	private Button btn_addline = new Button("Add");

	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Ok", VaadinIcons.CHECK);

	public CustomerOrderForm(CustomerOrderList owner, CustomerOrderRepository customerOrderRepository, String caption, CustomerOrderHead orderHead, int crudFunction) {

		this.owner = owner;
		this.customerOrderRepository = customerOrderRepository;
		this.crudFunction = crudFunction;

		switch (crudFunction) {
		case CustomerOrderList.CRUD_DELETE:
			btn_save.setCaption("Confirm delete");
			break;
		}
		initLayout(caption);
		initBehavior(orderHead);
	}

	private void initLayout(String caption) {
		
		grid.setHeightMode(HeightMode.UNDEFINED);
		setCaption(caption);
		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		buttons.setSpacing(true);

		// HEADER - registration / selection
		orderdate.addStyleName(ValoTheme.DATEFIELD_TINY);


		HorizontalLayout orderheader = new HorizontalLayout();
		orderheader.addComponent(orderdate);
		orderheader.addComponent(fnc.createPrompt(customernumber, name, btn_customernumber));

		HorizontalLayout orderline = new HorizontalLayout();
		orderline.addComponent(fnc.createPrompt(itemnumber, itemdescription, btn_itemnumber));
		
		units.addStyleName(ValoTheme.TEXTFIELD_TINY);
		priceperitem.addStyleName(ValoTheme.TEXTFIELD_TINY);
		btn_clearline.addStyleName(ValoTheme.BUTTON_TINY);
		btn_addline.addStyleName(ValoTheme.BUTTON_TINY);

		units.setValue("1");
		orderline.addComponent(units);
		orderline.addComponent(priceperitem);
		

		HorizontalLayout orderlineButtons = new HorizontalLayout();
		orderlineButtons.setSpacing(false);
		orderlineButtons.addComponent(btn_clearline);
		orderlineButtons.addComponent(btn_addline);
		orderline.addComponent(orderlineButtons);
		orderline.setComponentAlignment(orderlineButtons, Alignment.BOTTOM_LEFT);

		VerticalLayout orderform = new VerticalLayout();
		orderform.addComponent(orderheader);
		orderform.addComponent(orderline);

		// @formatter:off
		// grid show whats in the order 
	       Binder<CustomerOrderLineListView> binder = grid.getEditor().getBinder();

	        grid.addColumn(CustomerOrderLineListView::getItemnumber)
	        .setCaption("Itemnumber")
	        .setEditorBinding(binder
	                .forField(new TextField())
	                .withNullRepresentation("")
	                .withValidator(new BeanValidator(CustomerOrderLineListView.class, "itemnumber"))
	                .bind(CustomerOrderLineListView::getItemnumber, CustomerOrderLineListView::setItemnumber));
	        grid.addColumn(CustomerOrderLineListView::getDescription)
	        .setCaption("Description")
	        .setEditorBinding(binder
	                .forField(new TextField())
	                .withNullRepresentation("")
	                .withValidator(new BeanValidator(CustomerOrderLineListView.class, "description"))
	                .bind(CustomerOrderLineListView::getDescription, CustomerOrderLineListView::setDescription));
	        grid.addColumn(CustomerOrderLineListView::getUnits)
	        .setCaption("Units")
	        .setEditorBinding(binder
	                .forField(new TextField())
	                .withConverter(new StringToLongConverter("Please enter a number"))
	                .withValidator(new BeanValidator(CustomerOrderLineListView.class, "units"))
	                .bind(CustomerOrderLineListView::getUnits, CustomerOrderLineListView::setUnits));
	        grid.addColumn(CustomerOrderLineListView::getPriceperitem)
	        .setCaption("Price per item")
	        .setEditorBinding(binder
	                .forField(new TextField())
	                .withConverter(new StringToDoubleConverter("Please enter a number"))
	                .withValidator(new BeanValidator(CustomerOrderLineListView.class, "priceperitem"))
	                .bind(CustomerOrderLineListView::getPriceperitem, CustomerOrderLineListView::setPriceperitem));
	        grid.addColumn(CustomerOrderLineListView::getLinetotal)
	        .setCaption("Line total")
	        .setEditorBinding(binder
	                .forField(new TextField())
	                .withConverter(new StringToDoubleConverter("Please enter a number"))
	                .withValidator(new BeanValidator(CustomerOrderLineListView.class, "linetotal"))
	                .bind(CustomerOrderLineListView::getLinetotal, CustomerOrderLineListView::setLinetotal));
	        
		
	        grid.setSizeFull();
		
			// @formatter:on

		// VerticalLayout layout = new VerticalLayout(orderform, grid, buttons);
		VerticalLayout layout = new VerticalLayout();
		layout.addComponents(orderform, grid, buttons);
		setContent(layout);
		setModal(true);
		setSizeFull();
		// center();
	}

	private void initBehavior(CustomerOrderHead orderHead) {
		BeanValidationBinder<CustomerOrderHead> binder = new BeanValidationBinder<>(CustomerOrderHead.class);

		if (crudFunction == CustomerOrderList.CRUD_CREATE) {
			orderHead.setChangedby("test");
			orderHead.setStatus(1);
			orderdate.setValue(LocalDate.now());
		}

		// header

		btn_customernumber.addClickListener(e -> searchCustomer());
		btn_itemnumber.addClickListener(e -> searchItem());

		btn_cancel.addClickListener(e -> close());
		btn_save.addClickListener(e -> {
			try {
				binder.validate();
				binder.writeBean(orderHead);
				RestResponse<Item> rs = null;
				switch (crudFunction) {
				case CustomerOrderList.CRUD_CREATE:
					rs = customerOrderRepository.create(orderHead);
					break;
				case CustomerOrderList.CRUD_EDIT:
					rs = customerOrderRepository.update(orderHead);
					break;
				case CustomerOrderList.CRUD_DELETE:
					rs = customerOrderRepository.delete(orderHead);
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

	private Object searchItem() {

		SearchForm prompt = new SearchForm(SearchForm.ITEMS, itemnumber, itemdescription, priceperitem, customerOrderRepository);
		getUI().addWindow(prompt);
		return null;
	}

	private Object searchCustomer() {
		SearchForm prompt = new SearchForm(SearchForm.CUSTOMERS, customernumber, name, null, customerOrderRepository);
		getUI().addWindow(prompt);
		return null;
	}

}
