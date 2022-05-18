package com.tiffa.wd.elock.paperless.core;

import lombok.Data;

@Data
public class ChangePasswordModel {

	private String currentPassword;
	private String newPassword;
	private String confirmNewPassword;
	
}
