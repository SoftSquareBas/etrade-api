package com.tiffa.wd.elock.paperless.core.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ReportService {
	
	public byte[] download(final String outputFileName);
	
	public CompletableFuture<Map<String, Object>> generateReportAsync(String outputFileName, String module, String reportName, String exportType, Map<String, Object> params);
	
	public void createProcessingFile(String reportSessionId);
	
	public void removeProcessingFile(String reportSessionId);
	
	public void createErrorFile(String reportSessionId);
	
	public boolean isErrorFileExists(String reportSessionId);
	
	public boolean isProcessingFileExists(String reportSessionId);
	
	public boolean isGeneratedReportFileExists(final String outputFileName);

}
