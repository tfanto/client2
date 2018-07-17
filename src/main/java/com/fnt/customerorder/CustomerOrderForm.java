package com.fnt.customerorder;

import java.time.LocalDate;
import java.util.List;

import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.Item;
import com.fnt.sys.Fnc;
import com.fnt.sys.RestResponse;
import com.vaadin.data.BeanValidationBinder;
import com.vaadin.data.BindingValidationStatus;
import com.vaadin.data.ValidationException;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

public class CustomerOrderForm extends Window {

	private Fnc fnc = new Fnc();

	private static final long serialVersionUID = -214415727456373593L;
	int crudFunction = 1;
	CustomerOrderRepository customerOrderRepository = new CustomerOrderRepository();
	CustomerOrderList owner;

	// headerInfo
	private DateField orderdate = new DateField("Orderdate");
	private ComboBox<String> customernumber = new ComboBox<>("Customernumber");
	private ComboBox<String> name = new ComboBox<>("Name");
	// lineInfo
	
	private ComboBox<String> itemnumber = new ComboBox<>("Itemnumber");
	private ComboBox<String> itemndescription = new ComboBox<>("Description");
	private TextField numberofitems = new TextField("Units");
	private TextField priceperitem = new TextField("Price per Item");
	private TextField linetotal = new TextField("Line total");
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
		setCaption(caption);
		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		buttons.setSpacing(true);

		HorizontalLayout orderheader = new HorizontalLayout();
		orderheader.addComponent(orderdate);
		orderheader.addComponent(customernumber);
		orderheader.addComponent(name);

		HorizontalLayout orderline = new HorizontalLayout();

		orderline.addComponent(itemnumber);
		orderline.addComponent(itemndescription);
		orderline.addComponent(numberofitems);
		orderline.addComponent(priceperitem);
		orderline.addComponent(linetotal);
		
		HorizontalLayout orderlineButtons = new HorizontalLayout();

		orderlineButtons.addComponent(btn_clearline);
		orderlineButtons.addComponent(btn_addline);
		//orderlineButtons.setComponentAlignment(btn_clearline, Alignment.BOTTOM_LEFT);
		//orderlineButtons.setComponentAlignment(btn_addline, Alignment.BOTTOM_RIGHT);
		orderline.addComponent(orderlineButtons);
		orderline.setComponentAlignment(orderlineButtons, Alignment.BOTTOM_LEFT);
		

		

		

		VerticalLayout orderform = new VerticalLayout();
		orderform.addComponent(orderheader);
		orderform.addComponent(orderline);

		VerticalLayout layout = new VerticalLayout(orderform, buttons);
		layout.setComponentAlignment(orderform, Alignment.TOP_LEFT);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		setSizeFull();
		center();
	}

	private void initBehavior(CustomerOrderHead orderHead) {
		BeanValidationBinder<CustomerOrderHead> binder = new BeanValidationBinder<>(CustomerOrderHead.class);

		if (crudFunction == CustomerOrderList.CRUD_CREATE) {
			orderHead.setChangedby("test");
			orderHead.setStatus(1);
			orderdate.setValue(LocalDate.now());
		}

		// binder.forField(orderingpoint).withConverter(new
		// StringToIntegerConverter("Please enter a
		// number")).bind(Item::getOrderingpoint, Item::setOrderingpoint);
		// binder.forField(instock).withConverter(new StringToIntegerConverter("Please
		// enter a number")).bind(Item::getInstock, Item::setInstock);
		// binder.forField(price).withConverter(new StringToDoubleConverter("Please
		// enter a number")).bind(Item::getPrice,Item::setPrice);
		// binder.forField(purchaseprice).withConverter(new
		// StringToDoubleConverter("Please enter a
		// number")).bind(Item::getPurchaseprice, Item::setPurchaseprice);
		// binder.forField(customernumber).bind(CustomerOrderHead::get,
		// CustomerOrderHead::setDate);
		// binder.bindInstanceFields(this);
		// binder.readBean(orderHead);

		// btn_refresh.addClickListener(e -> fetchCustomer(customer.getId()));

		btn_cancel.addClickListener(e ->

		close());
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

}
