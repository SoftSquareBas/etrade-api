package com.tiffa.wd.elock.paperless;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.Module;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
/* @EnableWebMvc */
public class WebMvcConfiguration /* implements WebMvcConfigurer */ {
	
	@Bean
	public Module module() {
		log.info("Initial ObjectMapper custom module...");
		return new ObjectMapperCustomModule();
	}
	
	@Bean
	public Module companyBranchModule() {
		log.info("Initial CompanyBranch ObjectMapper module...");
		return new CompanyBranchObjectMapperModule();
	}
	
//	@Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//            	registry.addMapping("/**")
//    			.allowedMethods("*")
//    			.allowedHeaders("*")
//    			.allowCredentials(true)
//    			.allowedOrigins(
////    				"58.137.51.98:8080"
////    				, "58.137.51.98:8081"
//    				"http://localhost:4200"
//    				, "http://localhost:8080"
//    			);
//            }
//        };
//    }
	
//	@Override
//	public void addCorsMappings(CorsRegistry registry) {
//		registry.addMapping("/**")
//			.allowedMethods("*")
//			.allowedHeaders("*")
//			.allowCredentials(true)
//			.allowedOriginPatterns("*");
////			.allowedOrigins(
////				"58.137.51.98:8080"
////				, "58.137.51.98:8081"
////				, "http://localhost:4200"
////				, "http://localhost:8080"
////			);
//	}
	
//	@Qualifier("requestAsyncTaskExecutor")
//	@Autowired
//	private AsyncTaskExecutor requestAsyncTaskExecutor;
//
//	@Override
//	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
//		log.debug("extendMessageConverters... {}", converters);
//		for (HttpMessageConverter<?> converter : converters) {
//			if(converter instanceof MappingJackson2HttpMessageConverter) {
//				MappingJackson2HttpMessageConverter c = (MappingJackson2HttpMessageConverter)converter;
//				c.getObjectMapper().registerModule(new ObjectMapperCustomModule());
//			}
//		}
//	}
//
//	@Override
//	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//		long timeout = 60_000l;
//		log.info("configureAsyncSupport... timeout: {}", timeout);
//		configurer.setTaskExecutor(requestAsyncTaskExecutor);
//		configurer.setDefaultTimeout(timeout);
//	}
	
}
