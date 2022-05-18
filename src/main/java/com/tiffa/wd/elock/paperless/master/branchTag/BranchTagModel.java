package com.tiffa.wd.elock.paperless.master.branchTag;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class BranchTagModel extends CommonRequest implements PageRequest {

	private Integer tagId;
	private String tagCode;
	private String tagName;
	
}
