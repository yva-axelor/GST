package com.axelor.invoice.web;

import java.math.BigDecimal;

import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.party.db.Address;
import com.axelor.product.db.Product;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceLineController {

	public void setItem(ActionRequest request, ActionResponse response) {
	    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	    Product product = invoiceLine.getProduct();

	    response.setValue("item", product != null ? product.getFullName() : null);
	}

	public void setPrice(ActionRequest request, ActionResponse response) {
	    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	    Product product = invoiceLine.getProduct();

	    if (product != null) {
	        invoiceLine.setPrice(product.getSalePrice());
	        response.setValue("price", invoiceLine.getPrice());
	    }
	}

	public void setNetAmount(ActionRequest request, ActionResponse response) {
	    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	    BigDecimal qty = BigDecimal.valueOf(invoiceLine.getQty());
	    BigDecimal price = invoiceLine.getPrice();
	    BigDecimal netAmount = qty.multiply(price);
	    response.setValue("netAmount", netAmount);
	}

	public void setGstRate(ActionRequest request, ActionResponse response) {
	    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	    BigDecimal gstRate = invoiceLine.getProduct().getGstRate();
	    response.setValue("gstRate", gstRate);
	}

	public void setIGSTSGSTCGST(ActionRequest request, ActionResponse response) {	
	    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	    BigDecimal netAmount = invoiceLine.getNetAmount();
	    BigDecimal gstRate = invoiceLine.getGstRate();
	    Invoice invoice= request.getContext().getParent().asType(Invoice.class);
	    invoiceLine.setInvoice(invoice);
	    
	    System.out.println(invoiceLine.getInvoice());
	    
	    if (netAmount != null && gstRate != null && invoiceLine.getInvoice() != null) {
	    
	        Address invoiceAddress = invoiceLine.getInvoice().getInvoiceAddress();
	        System.out.println("invoice addresssssssss"+invoiceAddress);
	        Address companyAddress = invoiceLine.getInvoice().getCompany().getAddress();
	        System.out.println("comapany addresssssssss"+companyAddress);


	        if (invoiceAddress != null && companyAddress != null) {
	            boolean isStateDifferent = !invoiceAddress.getCity().getState().equals(companyAddress.getCity().getState());

	            if (isStateDifferent) {
	                BigDecimal igst = netAmount.multiply(gstRate);
	                response.setValue("igst", igst);
	            } else {
	                BigDecimal sgst = netAmount.multiply(gstRate.divide(BigDecimal.valueOf(2)));
	                BigDecimal cgst = sgst;
	                response.setValue("sgst", sgst);
	                response.setValue("cgst", cgst);
	            }
	        }
	    }
	}


	public void setGrossAmountWithTax(ActionRequest request, ActionResponse response) {
	    InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	    BigDecimal netAmount = invoiceLine.getNetAmount();
	    BigDecimal igst = invoiceLine.getIgst();
	    BigDecimal sgst = invoiceLine.getSgst();
	    BigDecimal cgst = invoiceLine.getCgst();

	    if (netAmount != null) {
	        BigDecimal tax = igst.add(sgst.add(cgst));
	        BigDecimal grossAmount = netAmount.add(tax);
	        response.setValue("grossAmount", grossAmount);
	    }
	}

}
