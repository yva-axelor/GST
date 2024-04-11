package com.axelor.product.web;

import java.math.BigDecimal;

import com.axelor.product.db.Product;
import com.axelor.product.db.ProductCategory;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class ProductController {
	public void setTaxRate(ActionRequest request,ActionResponse response) {
		Product product=request.getContext().asType(Product.class);
		ProductCategory productCategory=new ProductCategory();
		System.out.println("===========");
		BigDecimal taxRate= product.getProductCategory().getGstRate();
		response.setValue("gstRate", taxRate);
		
	}
}
