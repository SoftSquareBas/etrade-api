package com.tiffa.wd.elock.paperless.master.branchUser;

import java.time.LocalDate;
import java.util.List;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BranchUserModel extends CommonRequest implements PageRequest, ValidateRequest {

	private Integer userId;
	private String username;
	private String password;
	private String userFullName;
	private Boolean showOnlyExpireUsers;
	
	private LocalDate effectiveDate;
	private LocalDate expireDate;
	
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
