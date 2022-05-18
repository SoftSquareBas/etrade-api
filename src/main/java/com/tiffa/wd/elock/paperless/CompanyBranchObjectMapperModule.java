package com.tiffa.wd.elock.paperless;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyBranchObjectMapperModule extends SimpleModule {

	private static final long serialVersionUID = -1875132709340546893L;

	public CompanyBranchObjectMapperModule() {
		log.debug("CompanyBranchObjectMapperModule");
		
		addDeserializer(Integer.class, new StdScalarDeserializer<Integer>(Integer.class) {
			private static final long serialVersionUID = -3682669850924589396L;

			@Override
			public Integer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				String name = p.getCurrentName();
				if("companyId".equals(name)) {
					return SecurityUtils.processCompanyId(p.getValueAsInt());
				} else if("branchId".equals(name)) {
					return SecurityUtils.processBranchId(p.getValueAsInt());
				}
				return p.getValueAsInt();
			}
		});
	}
}
