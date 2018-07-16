package com.fnt.customerorder;

import java.util.List;

import com.fnt.customer.CustomerList;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.Item;
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

public class CustomerOrderForm extends Window {

	private static final long serialVersionUID = -214415727456373593L;
	int crudFunction = 1;
	CustomerOrderRepository customerOrderRepository = new CustomerOrderRepository();
	CustomerOrderList owner;

	private TextField itemnumber = new TextField("Itemnumber");
	private TextField description = new TextField("Description");
	private TextField orderingpoint = new TextField("Orderingpoint");
	private TextField instock = new TextField("In stock");
	private TextField price = new TextField("Price");
	private TextField purchaseprice = new TextField("Purchaseprice");

	private Button btn_cancel = new Button("Cancel");
	private Button btn_save = new Button("Ok", VaadinIcons.CHECK);
	// private Button btn_refresh = new Button("Refresh"); // does not work as
	// expected

	public CustomerOrderForm(CustomerOrderList owner, CustomerOrderRepository customerOrderRepository, String caption, CustomerOrderHead item, int crudFunction) {

		this.owner = owner;
		this.customerOrderRepository = customerOrderRepository;
		this.crudFunction = crudFunction;

		switch (crudFunction) {
		case CustomerOrderList.CRUD_DELETE:
			btn_save.setCaption("Confirm delete");
			break;
		}
		initLayout(caption);
		initBehavior(item);
	}

	private void initLayout(String caption) {
		setCaption(caption);
		btn_save.addStyleName(ValoTheme.BUTTON_PRIMARY);

		HorizontalLayout buttons = new HorizontalLayout(btn_cancel, btn_save);
		// HorizontalLayout buttons = new HorizontalLayout(btn_refresh, btn_cancel,
		// btn_save);
		buttons.setSpacing(true);

		GridLayout formLayout = new GridLayout(3, 6, itemnumber, description, orderingpoint, instock, price, purchaseprice);
		formLayout.setMargin(true);
		formLayout.setSpacing(true);

		VerticalLayout layout = new VerticalLayout(formLayout, buttons);
		layout.setComponentAlignment(buttons, Alignment.BOTTOM_RIGHT);
		setContent(layout);
		setModal(true);
		setSizeFull();
		center();
	}

	private void initBehavior(CustomerOrderHead obj) {
		BeanValidationBinder<CustomerOrderHead> binder = new BeanValidationBinder<>(CustomerOrderHead.class);

		if (crudFunction == CustomerList.CRUD_CREATE) {
			// obj.setOrderingpoint(0);
			// obj.setInstock(0);
			// obj.setPrice(0.0);
			// obj.setPurchaseprice(0.0);
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
		// binder.bindInstanceFields(this);
		binder.readBean(obj);

		// btn_refresh.addClickListener(e -> fetchCustomer(customer.getId()));

		btn_cancel.addClickListener(e ->

		close());
		btn_save.addClickListener(e -> {
			try {
				binder.validate();
				binder.writeBean(obj);
				RestResponse<Item> rs = null;
				switch (crudFunction) {
				case CustomerOrderList.CRUD_CREATE:
					rs = customerOrderRepository.create(obj);
					break;
				case CustomerOrderList.CRUD_EDIT:
					rs = customerOrderRepository.update(obj);
					break;
				case CustomerOrderList.CRUD_DELETE:
					rs = customerOrderRepository.delete(obj);
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
