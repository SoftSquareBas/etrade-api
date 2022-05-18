package com.tiffa.wd.elock.paperless.core.service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.tiffa.wd.elock.paperless.core.entity.Company;
import com.tiffa.wd.elock.paperless.core.exception.BusinessLogicException;
import com.tiffa.wd.elock.paperless.core.repository.CompanyRepository;

@Slf4j
@Service
public class GeneratorService {

	@Autowired
	private CompanyRepository companyRepository;

	@Transactional(propagation = Propagation.REQUIRED)
	public String generateTripId(final Integer companyId, final Integer branchId) {
		Company company = companyRepository.getByIdWithLock(companyId);
		log.debug("company : {}", company);

		if (company.getNoDigit() == null || company.getNoDigit() <= 0) {
			throw BusinessLogicException.error("Please define \"Trip ID\" format.");
		}

		StringBuilder dateFormat = new StringBuilder();

		if ("1".equals(company.getOpsYear())) {
			if ("YYYY".equals(company.getOpsYearFormat())) {
				dateFormat.append("yyyy");
			} else if ("YY".equals(company.getOpsYearFormat())) {
				dateFormat.append("yy");
			}
		}

		if ("1".equals(company.getOpsMonth())) {
			dateFormat.append("MM");
		}

		if ("1".equals(company.getOpsDay())) {
			dateFormat.append("dd");
		}

		String runningDate = LocalDateTime.now()
				.format(DateTimeFormatter.ofPattern(dateFormat.toString(), Locale.ENGLISH));

		String currentPrefixCode = String.format("%s%s", company.getCompanyCode().toUpperCase(), runningDate);

		log.debug("previousPrefixCode : {}", company.getPrefixCode());
		log.debug("currentPrefixCode : {}", currentPrefixCode);

		if (currentPrefixCode.equals(company.getPrefixCode())) {
			if (company.getCurrentRunning() == null || company.getCurrentRunning() <= 0L) {
				company.setCurrentRunning(1L);
			} else {
				company.setCurrentRunning(company.getCurrentRunning() + 1L);
			}
		} else {
			company.setPrefixCode(currentPrefixCode);
			company.setCurrentRunning(1L);
		}

		companyRepository.save(company);

		String runningDigit = String.format(MessageFormat.format("%0{0}d", company.getNoDigit()),
				company.getCurrentRunning());

		String tripId = String.format("%s%s", currentPrefixCode, runningDigit);

		log.debug("Generate Trip ID : {}", tripId);

		return tripId;
	}

	public String generateTrackId() {
		return String.format("%s%s", RandomStringUtils.randomAlphabetic(3), RandomStringUtils.randomNumeric(3));
	}

}
