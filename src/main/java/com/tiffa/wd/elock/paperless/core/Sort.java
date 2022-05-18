package com.tiffa.wd.elock.paperless.core;

import java.util.Comparator;

import org.springframework.data.domain.Sort.Direction;

import com.tiffa.wd.elock.paperless.core.util.CoreUtils;

import lombok.Data;

@Data
public class Sort {

	private String colId;
	private String sort;
	private Integer sortIndex;
	
	private boolean isCustom;
	private String customSort;
	
	public Sort() {
		this.isCustom = false;
	}
	
	private Sort(boolean isCustom) {
		this.isCustom = isCustom;
	}
	
	public static Sort by(String colId) {
		return by(colId, Direction.ASC);
	}
	
	public static Sort by(String colId, Direction direction) {
		Sort sort = new Sort();
		sort.setColId(colId);
		sort.setSort(direction.toString());
		return sort;
	}
	
	public static Sort custom(String customSort) {
		Sort sort = new Sort(true);
		sort.setCustomSort(customSort);
		return sort;
	}
	
	public static interface SqlOrderByProcessor {
		public String process(String alias, Sort sort);
	}
	
	public static class DefaultSqlOrderByProcessor implements SqlOrderByProcessor {

		@Override
		public String process(String alias, Sort s) {
			if(s.isCustom()) {
				return s.getCustomSort();
			} else {
				return String.format(" %s\"%s\" %s ", 
					(CoreUtils.isNotEmpty(alias) ? alias + "." : ""),
					s.getColId(),
					(CoreUtils.isNotEmpty(s.getSort()) ? s.getSort().toUpperCase() : "ASC"));
			}
		}
	}
	
	public static class SortComparator implements Comparator<Sort> {

		@Override
		public int compare(Sort o1, Sort o2) {
			if(o1.sortIndex < o2.sortIndex) {
				return -1;
			} else if(o1.sortIndex > o2.sortIndex) {
				return 1;
			}
			return 0;
		}
		
	}
}
