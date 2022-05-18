package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "product")
@Table(name = "product")
public class Product {

	@Id
	@SequenceGenerator(name = "product_seq", sequenceName = "product_seq", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "product_seq")
	@Column(name = "product_id", precision = 10)
	private Integer productId;

	@Column(name = "cpn_id", precision = 10)
	private Integer companyId;

	@Column(name = "branch_id", precision = 10)
	private Integer branchId;

	@Column(name = "product_code", length = 20)
	private String productCode;

	@Column(name = "product_description_en", length = 150)
	private String productDescriptionEn;

	@Column(name = "product_description_th", length = 150)
	private String productDescriptionTh;

	@Column(name = "uom_code", length = 20)
	private String uomCode;

}
