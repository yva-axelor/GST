package com.axelor.invoice.web;

import java.math.BigDecimal;

import com.axelor.invoice.db.InvoiceLine;
import com.axelor.party.db.Address;
import com.axelor.product.db.Product;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceLineController {

	public void setItem(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
		Product product = invoiceLine.getProduct();

		response.setValue("item", product.getFullName());

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

		BigDecimal qty = new BigDecimal(invoiceLine.getQty());
		BigDecimal price = invoiceLine.getPrice();
		BigDecimal total = qty.multiply(price);

		response.setValue("netAmount", total);
	}

	public void setGstRate(ActionRequest request, ActionResponse response) {

		InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);

		invoiceLine.setGstRate(invoiceLine.getProduct().getGstRate());

		response.setValue("gstRate", invoiceLine.getGstRate());

	}	
	
	
	  public void setIGSTSGSTCGST(ActionRequest request, ActionResponse response) {
	        InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
	        BigDecimal netAmount = invoiceLine.getNetAmount();
	        BigDecimal gstRate = invoiceLine.getGstRate();

	        if (netAmount != null && gstRate != null) {
	            Address invoiceAddress = invoiceLine.getInvoice().getInvoiceAddress();
	            Address companyAddress = invoiceLine.getInvoice().getCompany().getAddress();

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
	            BigDecimal grossAmount = netAmount.add(igst != null ? igst : sgst.add(cgst));
	            response.setValue("grossAmount", grossAmount);
	        }
	    }
	

}
