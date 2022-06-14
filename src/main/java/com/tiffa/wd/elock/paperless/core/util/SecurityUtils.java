package com.tiffa.wd.elock.paperless.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tiffa.wd.elock.paperless.TiffaAuthentication;

public final class SecurityUtils {
	
	public static Integer getTiffaCompanyId() {
		return 0;
	}
	
	public static Integer processCompanyId(Integer paramCompanyId) {
		Integer companyId = getCompanyId();
		if(CoreUtils.isNull(companyId)) {
			return paramCompanyId;
		}
		return companyId;
	}
	
	public static Integer processBranchId(Integer paramBranchId) {
		Integer branchId = getBranchId();
		if(CoreUtils.isNull(branchId)) {
			return paramBranchId;
		}
		return branchId;
	}

	public static Integer getCompanyId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication instanceof TiffaAuthentication) {
			return ((TiffaAuthentication) authentication).getCompanyId();
		}
		return null;
	}
	
	public static String getCompanyCode() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		if(authentication instanceof TiffaAuthentication) {
//			return ((TiffaAuthentication) authentication).getCompanyCode();
//		}
//		return null;
		return "000";
	}
	
	public static Integer getBranchId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication instanceof TiffaAuthentication) {
			return ((TiffaAuthentication) authentication).getBranchId();
		}
		return null;
	}
	
	public static String getBranchCode() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication instanceof TiffaAuthentication) {
			return ((TiffaAuthentication) authentication).getBranchCode();
		}
		return null;
	}

	public static Integer getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication instanceof TiffaAuthentication) {
			return ((TiffaAuthentication) authentication).getUserId();
		}
		return null;
	}
	
	public static String getUsername() {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		if(authentication instanceof TiffaAuthentication) {
//			return ((TiffaAuthentication) authentication).getUserLogin();
//		}
//		return null;
		return "admin";
	}

	public static String getDisplayName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if(authentication instanceof TiffaAuthentication) {
			return ((TiffaAuthentication) authentication).getDisplayName();
		}
		return null;
	}

}
