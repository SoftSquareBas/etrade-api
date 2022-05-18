package com.tiffa.wd.elock.paperless;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class TiffaAuthentication extends OAuth2Authentication {

	private static final long serialVersionUID = -2347087395188610267L;
	
	private final Integer userId;
	private final String userLogin;
	private final Integer companyId;
	private final String companyCode;
	private final Integer branchId;
	private final String branchCode;
	private final Integer isOfficer;
	private final String displayName;
	
	public TiffaAuthentication(OAuth2Authentication authentication,
			Integer userId,
			String userLogin, 
			Integer companyId, String companyCode,
			Integer branchId, String branchCode,
			String displayName, Integer isOfficer) {
		super(authentication.getOAuth2Request(), authentication.getUserAuthentication());
		this.userId = userId;
		this.userLogin = userLogin;
		this.displayName = displayName;
		this.companyId = companyId;
		this.companyCode = companyCode;
		this.branchId = branchId;
		this.branchCode = branchCode;
		this.isOfficer = isOfficer;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public String getUserLogin() {
		return userLogin;
	}

	public String getDisplayName() {
		return displayName;
	}
	
	public Integer getCompanyId() {
		return companyId;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public Integer getBranchId() {
		return branchId;
	}

	public String getBranchCode() {
		return branchCode;
	}

	public Integer isOfficer() {
		return isOfficer;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		sb.append("; userLogin: ").append(this.userLogin);
		sb.append("; displayName: ").append(this.displayName);
		sb.append("; companyId: ").append(this.companyId);
		sb.append("; companyCode: ").append(this.companyCode);
		sb.append("; branchId: ").append(this.branchId);
		sb.append("; branchCode: ").append(this.branchCode);
		sb.append("; isOfficer: ").append(this.isOfficer);
		return sb.toString();
	}

}
