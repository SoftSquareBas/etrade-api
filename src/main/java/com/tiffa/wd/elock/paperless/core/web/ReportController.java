package com.tiffa.wd.elock.paperless.core.web;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.WebAsyncTask;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.exception.ReportException;
import com.tiffa.wd.elock.paperless.core.model.ReportParam;
import com.tiffa.wd.elock.paperless.core.service.ReportService;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
import com.tiffa.wd.elock.paperless.core.util.Response;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@Slf4j
@RestController
@RequestMapping("/api/report")
public class ReportController {

	@Autowired
	private ReportService reportService;
	
	private final Integer TIMEOUT = 1000 * 60 * 60;

	@PostMapping(path = "/generate")
	public WebAsyncTask<ResponseEntity<byte[]>> generateSync(@RequestBody ReportParam param) {
		return new WebAsyncTask<ResponseEntity<byte[]>>(TIMEOUT, () -> {
			log.info("generate param : {}", param);
			Map<String, Object> params = getDefaultParameter(param);
			String outputFileName = createOutputFileName(param);
			param.setFilename(outputFileName);
	
			CompletableFuture<Map<String, Object>> p = reportService.generateReportAsync(outputFileName, param.getModule(), param.getReportName(), param.getExportType(), params);
	
			// Wait until process finish.
			Map<String, Object> result = p.join();
			if("error".equals(result.get("status"))) {
				Exception e = (Exception)result.get("exception");
				throw ReportException.error(e.getMessage(), e);
			}
			
			return downloadFile(param);
		});
	}
	
	@PostMapping(path = "/generate-async")
	public WebAsyncTask<Response> generateAsync(@RequestBody ReportParam param) {
		return new WebAsyncTask<Response>(TIMEOUT, () -> {
			log.info("generateAsync param : {}", param);
			Map<String, Object> params = getDefaultParameter(param);
			String outputFileName = createOutputFileName(param);
			param.setFilename(outputFileName);
	
			reportService.createProcessingFile(outputFileName);
			reportService.generateReportAsync(outputFileName, param.getModule(), param.getReportName(), param.getExportType(), params);
			
			return Response.success(Data.of("filename", outputFileName));
		});
	}
	
	@PostMapping(path = "/checking")
	public Callable<Response> checking(@RequestBody ReportParam param) {
		return () -> {
			log.info("checking param: {}", param);
			
			String filename = param.getFilename();
			Data data = Data.of("filename", filename);
	
			if(reportService.isErrorFileExists(filename)) {
				data.put("status", "error");
			} else if(reportService.isProcessingFileExists(filename)) {
				data.put("status", "processing");
			} else if(reportService.isGeneratedReportFileExists(filename)) {
				data.put("status", "finish");
			} else {
				data.put("status", "unknown");
			}
			
			return Response.success(data); 
		};
	}
	
	@PostMapping(path = "/download")
	public Callable<ResponseEntity<byte[]>> download(@RequestBody ReportParam param) {
		return () -> {
			return downloadFile(param);
		};
	}
	
	private ResponseEntity<byte[]> downloadFile(ReportParam param) {
		log.info("download param: {}", param);
		String filename = param.getFilename();
		return ResponseEntity.ok()
				.headers(getHttpHeaders(filename, getMediaType(param.getExportType())))
				.body(reportService.download(filename));
	}
	
	private Map<String, Object> getDefaultParameter(ReportParam param) {
		Map<String, Object> params = new HashMap<>();
		params.put("companyId", SecurityUtils.getCompanyId());
		params.put("username", SecurityUtils.getUsername());
		params.put("displayName", SecurityUtils.getDisplayName());
		params.putAll(param.getData());
		return params;
	}

	private String createOutputFileName(ReportParam param) {
		String outputFilename = null;
		String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
		if(CoreUtils.isEmpty(param.getFilename())) {
			outputFilename = String.format("%s-%s.%s%s", SecurityUtils.getCompanyCode(), param.getReportName(), currentTime, getFileExtension(param.getExportType()));
		} else {
			outputFilename = String.format("%s.%s%s", param.getFilename(), currentTime, getFileExtension(param.getExportType()));
		}
		return outputFilename;
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
	
	private String getFileExtension(String exportType) {
		String result;
		switch (exportType) {
		case "HTML":
			result = ".html";
			break;

		case "CSV":
			result = ".csv";
			break;

		case "XML":
			result = ".xml";
			break;

		case "XLSX":
			result = ".xlsx";
			break;

		case "PDF":
			result = ".pdf";
			break;

		default:
			Throwable t = new JRException("Unknown report format: " + exportType);
			throw ReportException.error(t.getMessage(), t);
		}

		return result;
	}

	private MediaType getMediaType(String exportType) {
		MediaType result;
		switch (exportType) {
		case "HTML":
			result = MediaType.TEXT_HTML;
			break;

		case "CSV":
			result = new MediaType("application","csv");
			break;

		case "XML":
			result = MediaType.APPLICATION_XML;
			break;

		case "XLSX":
			result = new MediaType("application","vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			break;

		case "PDF":
			result = MediaType.APPLICATION_PDF; //"application/x-download";
			break;

		default:
			Throwable t = new JRException("Unknown report format: " + exportType);
			throw ReportException.error(t.getMessage(), t);
		}

		return result;
	}
}
