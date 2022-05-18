package com.tiffa.wd.elock.paperless;

import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.web.cors")
public class CorsProperties extends CorsEndpointProperties {

	@Override
	public String toString() {
		StringBuilder text = new StringBuilder();
		text.append("AllowedOrigins=").append(getAllowedOrigins());
		text.append(", AllowedOriginPatterns=").append(getAllowedOriginPatterns());
		text.append(", AllowedMethods=").append(getAllowedMethods());
		text.append(", AllowedHeaders=").append(getAllowedHeaders());
		text.append(", ExposedHeaders=").append(getExposedHeaders());
		text.append(", AllowCredentials=").append(getAllowCredentials());
		text.append(", MaxAge()=").append(getMaxAge());

		return text.toString();
	}

}
