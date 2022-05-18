package com.tiffa.wd.elock.paperless.master.product;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class ProductModel extends CommonRequest implements PageRequest, ValidateRequest {

	private Integer productId;
	private String productCode;
	private String productDescEn;
	private String productDescTh;
	private String uom;
	
}
