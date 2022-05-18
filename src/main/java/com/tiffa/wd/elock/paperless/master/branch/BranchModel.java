package com.tiffa.wd.elock.paperless.master.branch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BranchModel extends CommonRequest implements PageRequest, ValidateRequest {

	private String companyCode;
	private String branchCode;
	private String branchName;
	private Integer branchStatus;
	
	private Integer branchUserId;
	private String branchUsername;
	private String branchPassword;
	private LocalDate branchExpireDate;
	
	private BigDecimal declarationCost;
	private BigDecimal webAppCost;
	private BigDecimal perTripCost;
	private String currency;
	private BigDecimal exchangeRate;
	
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
