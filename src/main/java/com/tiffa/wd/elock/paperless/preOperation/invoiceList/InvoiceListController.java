package com.tiffa.wd.elock.paperless.preOperation.invoiceList;

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
@RequestMapping("/api/pre-operation/invoice-list")
@PreAuthorize("hasAuthority('INVOICE_LISTING')")
public class InvoiceListController {
	
	@Autowired
	private InvoiceListService invoiceListService;
	
	@PostMapping("/search")
	public Callable<Response> search(@RequestBody InvoiceListModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = invoiceListService.search(model);

			return Response.success(gridData);
		};
	}
	
}
