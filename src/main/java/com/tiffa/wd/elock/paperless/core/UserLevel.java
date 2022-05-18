package com.tiffa.wd.elock.paperless.core;

public enum UserLevel {

	UNKNOWN(-1),
	ADMIN(0),
	COMPANY(1),
	BRANCH(2);
	
	private int value;
	
	private UserLevel(int value) {
		this.value = value;
	}
	
	public int value() {
		return this.value;
	}
}
