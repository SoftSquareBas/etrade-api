package com.tiffa.wd.elock.paperless.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.repository.CoreRepository;
import com.tiffa.wd.elock.paperless.core.repository.SqlParams;
import com.tiffa.wd.elock.paperless.core.repository.SqlTypeConversion;

@Service
@Transactional(readOnly = true, propagation = Propagation.NEVER)
public class UserService {

	@Autowired
	private CoreRepository coreRepository;
	
	public Data getUserInfo(String username) {
		SqlParams params = SqlParams.create("username", username);
		
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT u.cpn_id AS \"companyId\", ");
		sql.append("     c.cpn_code AS \"companyCode\",  ");
		sql.append("     b.branch_id AS \"branchId\",  ");
		sql.append("     b.branch_code AS \"branchCode\",  ");
		sql.append("     u.user_id AS \"userId\",  ");
		sql.append("     u.user_login AS \"userLogin\",  ");
		sql.append("     u.user_name AS \"displayName\", ");
		sql.append("     u.is_officer AS \"isOfficer\" ");
		sql.append(" FROM user_paperless u ");
		sql.append("     LEFT JOIN company c ON c.cpn_id = u.cpn_id ");
		sql.append("     LEFT JOIN branch b ON b.branch_id = u.branch_id ");
		sql.append(" WHERE LOWER(NVL(c.cpn_code,'-') || '\\' || NVL(b.branch_code,'-') || '\\' || u.user_login) = LOWER(:username) ");
		
		SqlTypeConversion conversion = SqlTypeConversion.create();
		conversion.add("userId", Integer.class);
		conversion.add("companyId", Integer.class);
		conversion.add("branchId", Integer.class);
		conversion.add("isOfficer", Integer.class);
		return coreRepository.getData(sql.toString(), params, conversion);
	}

}
