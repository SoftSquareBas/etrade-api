package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.ChangePasswordModel;
import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.entity.User;
import com.tiffa.wd.elock.paperless.core.exception.BusinessLogicException;
import com.tiffa.wd.elock.paperless.core.repository.UserRepository;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class AccountService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public Data changePassword(ChangePasswordModel model) {

		if (!model.getNewPassword().equals(model.getConfirmNewPassword())) {
			throw BusinessLogicException.error("Confirm new password doesn't match.");
		}

		User user = new User();
		user.setCompanyId(SecurityUtils.getCompanyId());
		user.setUserLogin(SecurityUtils.getUsername());

		user = userRepository.findOne(Example.of(user)).get();

		if (!passwordEncoder.matches(model.getCurrentPassword(), user.getUserPwd())) {
			throw BusinessLogicException.error("Current password doesn't match.");
		}

		user.setUserPwd(model.getNewPassword());
		userRepository.save(user);

		return Data.of();
	}

}
