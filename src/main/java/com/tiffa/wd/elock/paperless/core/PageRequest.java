package com.tiffa.wd.elock.paperless.core;

import java.util.List;

public interface PageRequest {

	public Integer getPageNumber();
	public Integer getPageSize();
	public List<Sort> getSorts();


}
