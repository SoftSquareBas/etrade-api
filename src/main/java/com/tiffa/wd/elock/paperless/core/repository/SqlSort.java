package com.tiffa.wd.elock.paperless.core.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.Sort;
import com.tiffa.wd.elock.paperless.core.Sort.SqlOrderByProcessor;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

public final class SqlSort {
	
	private List<Sort> sorts;
	private List<Sort> defaultSorts;
	
	private SqlOrderByProcessor processor;

	private SqlSort() {
	}

	public static SqlSort create(PageRequest pageRequest, Sort... defaultSorts) {
		SqlSort sort = new SqlSort();
		sort.sorts = pageRequest.getSorts();
		sort.defaultSorts = Arrays.asList(defaultSorts);
		sort.processor = new Sort.DefaultSqlOrderByProcessor();
		return sort;
	}

	public List<Sort> getSorts() {
		return sorts;
	}

	public List<Sort> getDefaultSorts() {
		return defaultSorts;
	}

	public String generate(String alias) {
		Pattern r = Pattern.compile("\\w+");

		List<String> s = new ArrayList<String>();

		if (CoreUtils.isNotEmpty(sorts)) {
			Collections.sort(sorts, new Sort.SortComparator());

			for (Sort sort : sorts) {
				if(r.matcher(sort.getSort()).matches()) {
					String t = processor.process(alias, sort);
					if(CoreUtils.isNotEmpty(t)) {
						s.add(t);
					}
				}
			}
		}

		if (CoreUtils.isNotEmpty(defaultSorts)) {
			for (Sort defaultSort : defaultSorts) {
				s.add(processor.process(alias, defaultSort));				
			}
		}

		if (CoreUtils.isNotEmpty(s)) {
			return " ORDER BY " + StringUtils.arrayToDelimitedString(s.toArray(), ",");
		}
		return "";
	}
}
