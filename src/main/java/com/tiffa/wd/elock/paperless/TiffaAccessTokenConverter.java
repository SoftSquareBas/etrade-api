package com.tiffa.wd.elock.paperless;

import java.util.Map;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.stereotype.Component;

@Component
public class TiffaAccessTokenConverter extends DefaultAccessTokenConverter {

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		Integer userId = (Integer) map.get("userId");
		String userLogin = (String) map.get("userLogin");
		String displayName = (String) map.get("displayName");
		Integer companyId = (Integer) map.get("companyId");
		String companyCode = (String) map.get("companyCode");
		Integer branchId = (Integer) map.get("branchId");
		String branchCode = (String) map.get("branchCode");
		Integer isOfficer = (Integer) map.get("isOfficer");
		
		TiffaAuthentication authentication = new TiffaAuthentication(
				super.extractAuthentication(map),
				userId,
				userLogin,
				companyId,
				companyCode, 
				branchId,
				branchCode,
				displayName, 
				isOfficer);

		return authentication;
	}

}
