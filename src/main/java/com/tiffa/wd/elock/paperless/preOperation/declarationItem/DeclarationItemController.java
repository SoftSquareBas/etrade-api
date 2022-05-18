package com.tiffa.wd.elock.paperless.preOperation.declarationItem;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.GridData;
import com.tiffa.wd.elock.paperless.core.exception.ReportException;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/pre-operation/declaration-item")
@PreAuthorize("hasAuthority('DEO2_TABVISIBLE')")
public class DeclarationItemController {
	
	@Autowired
	private DeclarationItemService declarationItemService;
	
	@Value("${app.declaration-item.pdf-report-path}")
	private String pdfReportPath;

	@PostMapping("/search")
	public Callable<Response> search(@RequestBody DeclarationItemModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = declarationItemService.search(model);

			return Response.success(gridData);
		};
	}
	
	@PostMapping("/getDetail")
	public Callable<Response> getDetail(@RequestBody DeclarationItemModel model) {
		return () -> {
			log.info("search model : {}", model);
			GridData gridData = declarationItemService.getDetail(model);

			return Response.success(gridData);
		};
	}
	
	@PostMapping(path = "/download")
	public Callable<ResponseEntity<byte[]>> download(@RequestBody DeclarationItemModel model) {
		return () -> {
			log.info("download model: {}", model);
			String filename = model.getReferenceNo() + ".pdf";
			try {
				return ResponseEntity.ok()
						.headers(getHttpHeaders(filename, MediaType.APPLICATION_PDF))
						.body(IOUtils.toByteArray(Paths.get(pdfReportPath, filename).toUri()));
			} catch (IOException e) {
				throw ReportException.error(e.getMessage(), e);
			}
		};
	}
	
	private HttpHeaders getHttpHeaders(String filename, MediaType mediaType) {
		HttpHeaders httpHeaders = new HttpHeaders();

		String name = filename.replaceAll("\\.[0-9]+\\.([a-z]{3,4})$", ".$1");
		httpHeaders.setContentType(mediaType);
		httpHeaders.setContentDisposition(ContentDisposition.inline().filename(name).build());
		httpHeaders.setAccessControlAllowCredentials(true);

		List<String> exposedHeaders = new ArrayList<String>();
		exposedHeaders.add("Content-Disposition");
		httpHeaders.setAccessControlExposeHeaders(exposedHeaders);
		return httpHeaders;
	}
}
