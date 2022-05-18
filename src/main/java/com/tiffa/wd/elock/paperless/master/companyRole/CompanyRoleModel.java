package com.tiffa.wd.elock.paperless.master.companyRole;

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
public class CompanyRoleModel extends CommonRequest implements PageRequest, ValidateRequest {

	private String companyCode;
	private Integer roleId;
	private String roleCode;
	private String roleName;
	
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
