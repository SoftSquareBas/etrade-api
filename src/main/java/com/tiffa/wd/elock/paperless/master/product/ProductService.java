package com.tiffa.wd.elock.paperless.master.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.entity.Product;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.ProductRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlSort;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.ValidationUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class ProductService {

	@Autowired
	private CoreRepository coreRepository;
	
	@Autowired
	private ProductRepository productRepository;

	public GridData search(final ProductModel model) {
		SqlParams params = SqlParams.createPageParam(model);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT a.*");
		sql.append(" FROM ( ");

		sql.append("   SELECT p.product_id AS \"productId\" ");
		sql.append("     , p.cpn_id AS \"companyId\" ");
		sql.append("     , c.cpn_code AS \"companyCode\" ");
		sql.append("     , p.branch_id AS \"branchId\" ");
		sql.append("     , b.branch_code AS \"branchCode\" ");
		sql.append("     , p.product_code AS \"productCode\" ");
		sql.append("     , p.product_description_en AS \"productDescEn\" ");
		sql.append("     , p.product_description_th AS \"productDescTh\" ");
		sql.append("     , p.uom_code AS \"uom\" ");
		sql.append("   FROM product p ");
		sql.append("     JOIN company c ON c.cpn_id = p.cpn_id ");
		sql.append("     JOIN branch b ON b.branch_id = p.branch_id ");
		sql.append("   WHERE 1 = 1 ");

		if (CoreUtils.isNotNull(model.getCompanyId())) {
			sql.append("    AND p.cpn_id = :companyId ");
			params.add("companyId", model.getCompanyId());
		}
		
		if (CoreUtils.isNotNull(model.getBranchId())) {
			sql.append("    AND p.branch_id = :branchId ");
			params.add("branchId", model.getBranchId());
		}
		
		if (CoreUtils.isNotNull(model.getProductCode())) {
			sql.append("    AND p.product_code LIKE :productCode ");
			params.add("productCode", "%" + model.getProductCode() + "%");
		}

		if (CoreUtils.isNotNull(model.getProductDescEn())) {
			sql.append("    AND p.product_description_en LIKE :productDescEn ");
			params.add("productDescEn", "%" + model.getProductDescEn() + "%");
		}
		
		if (CoreUtils.isNotNull(model.getProductDescTh())) {
			sql.append("    AND p.product_description_th LIKE :productDescTh ");
			params.add("productDescTh", "%" + model.getProductDescTh() + "%");
		}
		
		if (CoreUtils.isNotNull(model.getUom())) {
			sql.append("    AND p.uom_code = :uom ");
			params.add("uom", model.getUom());
		}
		
		sql.append(" ) a ");
		SqlSort sort = SqlSort.create(model, Sort.by("companyCode", Direction.ASC), Sort.by("branchCode",  Direction.ASC), Sort.by("productCode", Direction.ASC));

		return coreRepository.searchPagingGridData(sql.toString(), params, sort);
	}
	
	@Transactional
	public Data add(final ProductModel model) {
		ValidationUtils.checkRequired(model.getCompanyId(), "Company Code");
		ValidationUtils.checkRequired(model.getBranchId(), "Branch Code");
		ValidationUtils.checkRequired(model.getProductCode(), "Product Code");
		ValidationUtils.checkRequired(model.getProductDescEn(), "Product Desc (En)");
		ValidationUtils.checkRequired(model.getProductDescTh(), "Product Desc (Th)");
		ValidationUtils.checkRequired(model.getUom(), "Uom");
		
		model.setValidateModel("productCode", model.getProductCode());
		ValidationUtils.checkDuplicate(validate(model), "Product Code");

		Product product = new Product();
		product.setCompanyId(model.getCompanyId());
		product.setBranchId(model.getBranchId());
		product.setProductCode(model.getProductCode());
		product.setProductDescriptionEn(model.getProductDescEn());
		product.setProductDescriptionTh(model.getProductDescTh());
		product.setUomCode(model.getUom());

		Integer productId = productRepository.saveAndFlush(product).getProductId();
		model.setProductId(productId);
		return Data.of(model);
	}
	
	@Transactional
	public Data edit(final ProductModel model) {
		ValidationUtils.checkRequired(model.getProductDescEn(), "Product Desc (En)");
		ValidationUtils.checkRequired(model.getProductDescTh(), "Product Desc (Th)");
		ValidationUtils.checkRequired(model.getUom(), "Uom");
		
		Product product = productRepository.getById(model.getProductId());
		product.setProductDescriptionEn(model.getProductDescEn());
		product.setProductDescriptionTh(model.getProductDescTh());
		product.setUomCode(model.getUom());
		
		productRepository.saveAndFlush(product);
		return Data.of(model);
	}

	@Transactional
	public Data delete(final ProductModel model) {
		productRepository.deleteById(model.getProductId());
		return Data.of();
	}
	
	public Data validate(final ProductModel model) {
		SqlParams params = SqlParams.createValidateParam(model);
		params.add("companyId", model.getCompanyId());
		params.add("branchId", model.getBranchId());
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT CASE WHEN COUNT(1) > 0 THEN 1 ELSE 0 END AS \"duplicate\" ");
		sql.append(" FROM product p ");
		sql.append(" WHERE p.cpn_id = :companyId ");
		sql.append("    AND p.branch_id = :branchId ");
		sql.append("    AND p.product_code = :driverCode ");
		
		return coreRepository.validate(sql.toString(), params);	
	}
	
}
