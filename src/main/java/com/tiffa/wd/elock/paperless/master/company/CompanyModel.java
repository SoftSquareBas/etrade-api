package com.tiffa.wd.elock.paperless.master.company;

import java.time.LocalDate;
import java.util.List;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class CompanyModel extends CommonRequest implements PageRequest, ValidateRequest  {
	
	private String companyCode;
	private Integer companyStatus;
	private String companyName;
	private String companyFullName;
	private String companyAddress;

	private String companyTaxId;
	private String companyTelephone;
	private String companyFax;
	private String companyEstablishNo;
	
	private String tripInfoYear;
	private String tripInfoYearFormat;
	
	private Boolean tripInfoYearYYYY;
	private Boolean tripInfoYearYY;
	
	private Integer tripInfoNumberOfDigit;
	private Boolean tripInfoMonthMM;
	private String tripInfoProfixCode;
	private Long tripInfoCurrentRunning;
	private Boolean tripInfoDayDD;
	
	private Boolean beginJourneyShow;
	private Boolean beginJourneyAutofill;
	
	private Boolean endJourneyShow;
	private Boolean endJourneyAutofill;
	
	private Boolean disarmShow;
	private Boolean disarmAutofill;
	
	private Integer companyUserId;
	private String companyUsername;
	private String companyPassword;
	private LocalDate companyExpireDate;
	
	private List<TransferItem> items;
	
	@Data
	static class TransferItem {
		private Integer key;
		private String title;
		private String type;
		private String source;
		private String direction;
	}

}
