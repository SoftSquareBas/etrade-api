package com.tiffa.wd.elock.paperless;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.tiffa.wd.elock.paperless.core.service.TiffaUserDetailsService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private TiffaUserDetailsService userDetailsService;

	/** For Authentication Server **/

	@Bean
	public CorsFilter corsFilter(CorsProperties properties) {
		log.info("Initial CorsFilter : {}", properties);

		CorsConfiguration config = properties.toCorsConfiguration();
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		for (String allowedOrigins : config.getAllowedOrigins()) {
			log.info("allowedOrigins : {}", allowedOrigins);
		}

		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Override
	@Autowired
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				// cors().and()
				.csrf().csrfTokenRepository(new CookieCsrfTokenRepository())
				.and().httpBasic()
				.and().authorizeRequests()
				.anyRequest().authenticated()
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().anonymous().disable()
				.exceptionHandling().authenticationEntryPoint(new BasicAuthenticationEntryPoint());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	/** For Resource Server **/

	@Override
	public void configure(WebSecurity web) {
		web.ignoring()
				.antMatchers(HttpMethod.OPTIONS)
				.antMatchers("/version/**")
				.antMatchers("/api/**")
				.antMatchers("/stport02/**")
				.antMatchers("/actuator/**")
				.antMatchers("/rt02/**")
				.antMatchers("/rt05/**");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
