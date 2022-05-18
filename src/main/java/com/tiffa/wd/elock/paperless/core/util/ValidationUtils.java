package com.tiffa.wd.elock.paperless.core.util;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.exception.BusinessLogicException;

public final class ValidationUtils {

	public static void checkRequired(String object, String fieldName) {
		if (CoreUtils.isEmpty(object)) {
			throw BusinessLogicException.required(fieldName);
		}
	}
	
	public static void checkRequired(Object object, String fieldName) {
		if (CoreUtils.isNull(object)) {
			throw BusinessLogicException.required(fieldName);
		}
	}
	
	public static void checkDuplicate(Data data, String fieldName) {
		if(CoreUtils.isNotNull(data) && data.isNotNull() && Boolean.TRUE.equals(data.get("duplicate"))) {
			throw BusinessLogicException.duplicated(fieldName);
		}
	}
}
