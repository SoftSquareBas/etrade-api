package com.tiffa.wd.elock.paperless.core.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tiffa.wd.elock.paperless.core.exception.ReportException;
import com.tiffa.wd.elock.paperless.core.util.TypeConvertionUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXmlExporterOutput;

@Slf4j
@Service
public class JasperReportsService implements ReportService {

	private DataSource dataSource;

	@Value("${spring.profiles.active:}")
	private String activeProfile;

	@Value("${app.report.location}")
	private Resource reportLocation;
	
	@Value("#{systemProperties['java.io.tmpdir']}")
	private String tempLocation;
	
	@Value("${app.report.virtualizer.max-size}")
	private int virtualizerMaxSize;

	@Autowired
	public JasperReportsService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	@Async("reportTaskExecutor")
	public CompletableFuture<Map<String, Object>> generateReportAsync(final String outputFileName, final String module,final String reportName,final String exportType,final Map<String, Object> params) {
		Map<String, Object> result = new HashMap<>();
		result.put("status", "success");
		try {
			Connection connection = null;
			JasperReport jasperReport = null;
			try {
				long startTime = System.currentTimeMillis();
				log.info("Async Generating... {} {} {}", module, reportName, exportType);
				log.info("  Start {} generating... {}", outputFileName, startTime);
				log.info("    Report Location : {}", reportLocation);
				log.info("    Temp Location : {}", tempLocation);

				String reportPath = Paths.get(module, reportName).toString();

				if ("development".equals(activeProfile)) {
					String jrxml = loadJrxmlFile(reportPath);
					jasperReport = JasperCompileManager.compileReport(jrxml);
				} else {
					if (jasperFileExists(reportPath)) {
						jasperReport = (JasperReport) JRLoader.loadObject(loadJasperFile(reportPath));
					} else {
						String jrxml = loadJrxmlFile(reportPath);
						jasperReport = JasperCompileManager.compileReport(jrxml);
						JRSaver.saveObject(jasperReport, loadJasperFile(reportPath));
					}
				}

				connection = DataSourceUtils.getConnection(dataSource);
				JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, gerReportParameter(jasperReport, params), connection);

				Path path = Paths.get(FileUtils.getTempDirectoryPath(), outputFileName);
				
				final OutputStream out = new FileOutputStream(new File(path.toString()));
				export(jasperPrint, exportType, out);

				long finishTime = System.currentTimeMillis();
				log.info("  Finish {} generating... {}", outputFileName, finishTime);
				log.info("Total time: {} second(s).", (finishTime - startTime)/1000);
			} finally {
				DataSourceUtils.releaseConnection(connection, dataSource);
				this.removeProcessingFile(outputFileName);
			}
		} catch (Exception e) {
			this.createErrorFile(outputFileName);
			result.put("status", "error");
			result.put("exception", e);
			log.error(e.getMessage(), e);
		}
		
		return CompletableFuture.completedFuture(result);
	}
	
	@Override
	public void createProcessingFile(String reportSessionId) {
		Path path = Paths.get(tempLocation, String.format("%s.%s", reportSessionId, "processing"));
		File file = new File(path.toString());
		FileUtils.deleteQuietly(file);
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw ReportException.error(e.getMessage(), e);
		}
	}

	@Override
	public void removeProcessingFile(String reportSessionId) {
		Path path = Paths.get(tempLocation, String.format("%s.%s", reportSessionId, "processing"));
		File file = new File(path.toString());
		FileUtils.deleteQuietly(file);
	}
	
	@Override
	public void createErrorFile(String reportSessionId) {
		Path path = Paths.get(tempLocation, String.format("%s.%s", reportSessionId, "error"));
		File file = new File(path.toString());
		FileUtils.deleteQuietly(file);
		try {
			file.createNewFile();
		} catch (IOException e) {
			throw ReportException.error(e.getMessage(), e);
		}
	}
	
	@Override
	public byte[] download(final String outputFileName) {
		try {
			Path path = Paths.get(tempLocation, outputFileName);
			return Files.readAllBytes(path);
		} catch (IOException e) {
			throw ReportException.error(e.getMessage(), e);
		}
	}
	
	@Override
	public boolean isErrorFileExists(String reportSessionId) {
		Path path = Paths.get(tempLocation, String.format("%s.%s", reportSessionId, "error"));
		File file = new File(path.toString());
		return file.exists();
	}

	@Override
	public boolean isProcessingFileExists(String reportSessionId) {
		Path path = Paths.get(tempLocation, String.format("%s.%s", reportSessionId, "processing"));
		File file = new File(path.toString());
		return file.exists();
	}

	@Override
	public boolean isGeneratedReportFileExists(String outputFileName) {
		Path path = Paths.get(tempLocation, outputFileName);
		File file = new File(path.toString());
		return file.exists();
	}

	private Map<String, Object> gerReportParameter(JasperReport jasperReport, Map<String, Object> param) throws IOException {
		Map<String, Object> params = new HashMap<>();
		params.put(JRParameter.REPORT_VIRTUALIZER, new JRFileVirtualizer(virtualizerMaxSize, tempLocation));
		params.put("RESOURCE_DIR", reportLocation.getURL().toString());
		
		Map<String, Class<?>> jrParam = new  HashMap<String, Class<?>>();
		for (JRParameter jrParameter : jasperReport.getParameters()) {
			if(!jrParameter.isSystemDefined()) {
				jrParam.put(jrParameter.getName(), jrParameter.getValueClass());
			}
		}

		for (Map.Entry<String, Object> p : param.entrySet()) {
			String name = p.getKey();
			Object value = p.getValue();
			Class<?> type = jrParam.get(name);
			if(value != null && type != null && !value.getClass().equals(type)) {
				log.info("convert {} {} [{} => {}]", name, value, value.getClass(), type);
				value = TypeConvertionUtils.convert(value, type);
			}
			params.put(name, value);
		}
		
		return params;
	}

	private void export(final JasperPrint print, String exportType, final OutputStream out) throws JRException {
		switch (exportType) {
		case "HTML": {
			HtmlExporter exporter = new HtmlExporter();
			exporter.setExporterOutput(new SimpleHtmlExporterOutput(out));
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.exportReport();
		}
			break;

		case "CSV": {
			JRCsvExporter exporter = new JRCsvExporter();
			exporter.setExporterOutput(new SimpleWriterExporterOutput(out));
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.exportReport();
		}
			break;

		case "XML": {
			JRXmlExporter exporter = new JRXmlExporter();
			exporter.setExporterOutput(new SimpleXmlExporterOutput(out));
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.exportReport();
		}
			break;

		case "XLSX": {
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.exportReport();
		}
			break;

		case "PDF": {
			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
			exporter.setExporterInput(new SimpleExporterInput(print));
			exporter.exportReport();
		}
			break;

		default:
			Throwable t = new JRException("Unknown report format: " + exportType);
			throw ReportException.error(t.getMessage(), t);
		}
	}

	private boolean jasperFileExists(String file) throws IOException {
		Path reportFile = Paths.get(reportLocation.getURI());
		reportFile = reportFile.resolve(file + ".jasper");
		if (Files.exists(reportFile))
			return true;
		return false;
	}

	private String loadJrxmlFile(String file) throws IOException {
		Path reportFile = Paths.get(reportLocation.getURI());
		reportFile = reportFile.resolve(file + ".jrxml");
		return reportFile.toString();
	}

	private File loadJasperFile(String file) throws IOException {
		Path reportFile = Paths.get(reportLocation.getURI());
		;
		reportFile = reportFile.resolve(file + ".jasper");
		return reportFile.toFile();
	}
}