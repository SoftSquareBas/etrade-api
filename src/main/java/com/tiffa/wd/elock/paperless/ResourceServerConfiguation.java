package com.tiffa.wd.elock.paperless;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableResourceServer
public class ResourceServerConfiguation extends ResourceServerConfigurerAdapter {

	@Autowired
    private TiffaAccessTokenConverter tiffaAccessTokenConverter;
	
	@Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenServicesForResource());
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServicesForResource() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStoreForResource());
        return defaultTokenServices;
    }

    @Bean
    public TokenStore tokenStoreForResource() {
        return new JwtTokenStore(accessTokenConverterForResource());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverterForResource() {
    	JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        ClassPathResource resource = new ClassPathResource("public.pem");
        String publicKey = null;
        try {
			publicKey = IOUtils.toString(resource.getInputStream(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
        converter.setVerifierKey(publicKey);
        converter.setAccessTokenConverter(tiffaAccessTokenConverter);
        return converter;
    }
    
    @Override
	public void configure(HttpSecurity http) throws Exception {       
        http
	    	.antMatcher("/**").authorizeRequests()
	    	.anyRequest().authenticated()
	        .and().csrf().disable().httpBasic()
	        .and().cors()
	        .and().exceptionHandling().accessDeniedPage("/403")
	        .and().exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
	}    
	
}
