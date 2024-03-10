package com.axelor.invoice.web;

import java.math.BigDecimal;

import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.party.db.Address;
import com.axelor.party.db.Contact;
import com.axelor.party.db.Party;
import com.axelor.party.db.repo.AddressRepository;
import com.axelor.party.db.repo.ContactRepository;
import com.axelor.party.db.repo.PartyRepository;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceController {

	private final ContactRepository contactRepo;
	private final AddressRepository addressRepo;
	private final PartyRepository partyRepo;

	public InvoiceController(ContactRepository contactRepo, AddressRepository addressRepo, PartyRepository partyRepo) {
		this.contactRepo = contactRepo;
		this.addressRepo = addressRepo;
		this.partyRepo = partyRepo;
	}

	public void setDefaultPartyContact(ActionRequest request, ActionResponse response) {
		Party party = request.getContext().asType(Party.class);
		if (party != null && party.getContactList() != null) {
			Contact defaultContact = null;
			for (Contact contact : party.getContactList()) {
				if ("primary".equals(contact.getTypeSelect())) {
					defaultContact = contact;
					break;
				}
			}
			if (defaultContact != null) {
				response.setValue("partyContact", defaultContact);
			}
		}
	}

	public void setDefaultInvoiceAddress(ActionRequest request, ActionResponse response) {
		Party party = request.getContext().asType(Party.class);
		if (party != null) {
			Address defaultAddress = null;

			if (party.getAddressList() != null) {
				for (Address address : party.getAddressList()) {
					if ("Default".equals(address.getTypeSelect()) || "Invoice".equals(address.getTypeSelect())) {
						defaultAddress = address;
						break;
					}
				}
			}

			if (defaultAddress != null) {
				response.setValue("invoiceAddress", defaultAddress);
			}
		}
	}

	public void setDefaultShippingAddress(ActionRequest request, ActionResponse response) {
		Party party = request.getContext().asType(Party.class);

		boolean useInvoiceAddressAsShipping = (boolean) request.getContext().get("useInvoiceAddressAsShipping");

		if (!useInvoiceAddressAsShipping && party != null && party.getAddressList() != null) {
			Address defaultShippingAddress = null;
			for (Address address : party.getAddressList()) {
				if ("shipping".equals(address.getTypeSelect())) {
					defaultShippingAddress = address;
					break;
				}
			}
			if (defaultShippingAddress != null) {
				response.setValue("shippingAddress", defaultShippingAddress);
			}
		}
	}

	public void calculateNetAmount(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);

		BigDecimal netAmount = BigDecimal.ZERO;

		if (invoice != null && invoice.getInvoiceItemsList() != null) {
			for (InvoiceLine line : invoice.getInvoiceItemsList()) {
				netAmount = netAmount.add(line.getNetAmount());

			}
		}

		response.setValue("netAmount", netAmount);
	}

	public void calculateIGST(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		BigDecimal igst = BigDecimal.ZERO;

		if (invoice != null && invoice.getInvoiceItemsList() != null) {
			for (InvoiceLine line : invoice.getInvoiceItemsList()) {
				igst = igst.add(line.getIgst());
			}
		}

		response.setValue("igst", igst);
	}

	public void calculateCGST(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		BigDecimal cgst = BigDecimal.ZERO;

		if (invoice != null && invoice.getInvoiceItemsList() != null) {
			for (InvoiceLine line : invoice.getInvoiceItemsList()) {
				cgst = cgst.add(line.getCgst());
			}
		}

		response.setValue("cgst", cgst);
	}

	public void calculateSGST(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		BigDecimal sgst = BigDecimal.ZERO;

		if (invoice != null && invoice.getInvoiceItemsList() != null) {
			for (InvoiceLine line : invoice.getInvoiceItemsList()) {
				sgst = sgst.add(line.getSgst());
			}
		}

		response.setValue("sgst", sgst);
	}

	public void calculateGrossAmount(ActionRequest request, ActionResponse response) {
		Invoice invoice = request.getContext().asType(Invoice.class);
		BigDecimal grossAmount = BigDecimal.ZERO;

		if (invoice != null && invoice.getInvoiceItemsList() != null) {
			for (InvoiceLine line : invoice.getInvoiceItemsList()) {
				grossAmount = grossAmount.add(line.getNetAmount());
			}
		}

		response.setValue("grossAmount", grossAmount);
	}

}
