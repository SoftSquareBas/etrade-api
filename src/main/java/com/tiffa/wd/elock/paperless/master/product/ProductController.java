package com.tiffa.wd.elock.paperless.master.product;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/master/product")
@PreAuthorize("hasAuthority('PRODUCT_TABVISIBLE')")
public class ProductController {

	@Autowired
	private ProductService productService;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody ProductModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = productService.search(model);
			return Response.success(gridData);
		};
	}

	@PostMapping("/add")
	@PreAuthorize("hasAuthority('PRODUCT_ADD')")
	public Callable<Response> add(@RequestBody ProductModel model) {
		return () -> {
			log.info("create model : {}", model);
			return Response.success(productService.add(model));
		};
	}

	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('PRODUCT_EDIT')")
	public Callable<Response> edit(@RequestBody ProductModel model) {
		return () -> {
			log.info("edit model : {}", model);
			return Response.success(productService.edit(model));
		};
	}

	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('PRODUCT_DELETE')")
	public Callable<Response> delete(@RequestBody ProductModel model) {
		return () -> {
			log.info("delete model : {}", model);
			productService.delete(model);
			return Response.success(productService.search(model));
		};
	}

	@PostMapping("/validate")
	@PreAuthorize("hasAnyAuthority('PRODUCT_ADD', 'PRODUCT_EDIT')")
	public Callable<Response> validate(@RequestBody ProductModel model) {
		return () -> {
			log.info("exists model : {}", model);
			return Response.success(productService.validate(model));
		};
	}

}
