package com.tiffa.wd.elock.paperless;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import com.tiffa.wd.elock.paperless.core.TiffaUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TiffaTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		//log.debug("enhance > authentication : {}", authentication);

		TiffaUser user = (TiffaUser)authentication.getPrincipal();
		
		final Map<String, Object> additionalInformation = new HashMap<>();
		additionalInformation.put("userId", user.getUserId());
		additionalInformation.put("userLogin", user.getUserLogin());
		additionalInformation.put("displayName", user.getDisplayName());
		additionalInformation.put("companyId", user.getCompanyId());
        additionalInformation.put("companyCode", user.getCompanyCode());
        additionalInformation.put("branchId", user.getBranchId());
        additionalInformation.put("branchCode", user.getBranchCode());
        additionalInformation.put("isOfficer", user.isOfficer());
        additionalInformation.put("level", user.getLevel().value());

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
        log.debug("enhance > accessToken : {}", accessToken);
        return accessToken;
	}
}
