package com.tiffa.wd.elock.paperless.core.web;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tiffa.wd.elock.paperless.core.ChangePasswordModel;
import com.tiffa.wd.elock.paperless.core.service.AccountService;
import com.tiffa.wd.elock.paperless.core.util.Response;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/account")
public class AccountController {
	
	@Autowired
	private AccountService accountService;
	
	@PostMapping("/change-password")
	public Callable<Response> changePassword(@RequestBody ChangePasswordModel model) {
		return () -> {
			log.info("changePassword model : {}", model);
			return Response.success(accountService.changePassword(model));
		};
	}

}
