package com.tiffa.wd.elock.paperless.master.tag;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class TagModel extends CommonRequest implements PageRequest, ValidateRequest {

	private Integer tagId;
	private String tagCode;
	private String tagName;
	private String tagStatus;
	
}
