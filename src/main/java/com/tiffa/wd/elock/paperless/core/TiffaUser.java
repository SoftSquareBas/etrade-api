package com.tiffa.wd.elock.paperless.core;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class TiffaUser extends User {

	private static final long serialVersionUID = -2177804053295045131L;
	
	private final Integer userId;
	private final String userLogin;
	private final Integer companyId;
	private final String companyCode;
	private final Integer branchId;
	private final String branchCode;
	private final Integer isOfficer;
	private final String displayName;
	
	private final UserLevel level; // 1 = Administrator, 2 = Company, 3 = Branch

	public TiffaUser(UserDetails user, 
			Integer userId,
			String userLogin, String displayName, 
			Integer companyId, String companyCode,
			Integer branchId, String branchCode,
			Integer isOfficer) {
		super(user.getUsername(), user.getPassword(), user.isEnabled(), 
				user.isAccountNonExpired(), user.isCredentialsNonExpired(), 
				user.isAccountNonLocked(), user.getAuthorities());
		this.userId = userId;
		this.userLogin = userLogin;
		this.displayName = displayName;
		this.companyId = Integer.valueOf(0).equals(companyId) ? null : companyId;
		this.companyCode = companyCode;
		this.branchId = branchId;
		this.branchCode = branchCode;
		this.isOfficer = isOfficer;
		
		// Define user level
		if(Integer.valueOf(0).equals(companyId)) {
			this.level = UserLevel.ADMIN;
		} else if(companyId > 0 && branchId == null) {
			this.level = UserLevel.COMPANY;
		} else if(companyId > 0 && branchId > 0) {
			this.level = UserLevel.BRANCH;
		} else {
			this.level = UserLevel.UNKNOWN;
		}
	}
	
	public Integer getUserId() {
		return userId;
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

	public String getUserLogin() {
		return userLogin;
	}

	public UserLevel getLevel() {
		return level;
	}

}
