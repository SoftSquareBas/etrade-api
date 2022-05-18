package com.tiffa.wd.elock.paperless.core.service;

import java.sql.Types;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Service;

import com.tiffa.wd.elock.paperless.core.Data;
import com.tiffa.wd.elock.paperless.core.TiffaUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TiffaUserDetailsService extends JdbcDaoImpl {

	@Autowired
	private UserService userService;
	
	@Autowired
	public TiffaUserDetailsService(DataSource dataSource) {
		super();
		setDataSource(dataSource);
		setUsersByUsernameQuery(
			" SELECT NVL(c.cpn_code,'-') || '\\' || NVL(b.branch_code,'-') || '\\' || u.user_login AS username, u.user_pwd AS password, 1 AS active " +  
			" 	, 0 AS accLocked, (CASE WHEN SYSDATE < NVL(user_expiredate, TO_DATE('31/12/9999', 'DD/MM/YYYY')) THEN 0 ELSE 1 END) AS accExpired, 0 AS credsExpired " + 
			" FROM user_paperless u  " +
			" 	LEFT JOIN company c ON c.cpn_id = u.cpn_id " +  
			"	LEFT JOIN branch b ON b.branch_id = u.branch_id " + 
			" WHERE LOWER(NVL(c.cpn_code,'-') || '\\' || NVL(b.branch_code,'-') || '\\' || u.user_login) = LOWER(?) " + 
			"   AND ROWNUM = 1 "
		);

		setAuthoritiesByUsernameQuery(
			" SELECT DISTINCT NVL(c.cpn_code,'-') || '\\' || NVL(b.branch_code,'-') || '\\' || u.user_login AS username, p.privilege AS authority " +  
			" FROM user_paperless u  " + 
			" 	JOIN se_user_privilege sup ON sup.user_id = u.user_id " + 
			"   JOIN se_privilege p ON p.privilege_id = sup.privilege_id " + 
			"   LEFT JOIN company c ON c.cpn_id = u.cpn_id " + 
			"   LEFT JOIN branch b ON b.branch_id = u.branch_id " + 
			" WHERE LOWER(NVL(c.cpn_code,'-') || '\\' || NVL(b.branch_code,'-') || '\\' || u.user_login) = LOWER(?) " 
		);

		setEnableGroups(true);
		setGroupAuthoritiesByUsernameQuery(
			" SELECT DISTINCT r.role_id, r.rolename, p.privilege " + 
			" FROM se_user_role ur " +
			"    JOIN se_role r ON r.role_id = ur.role_id " +
			"    JOIN se_role_privilege rp ON rp.role_id = r.role_id " +
			"    JOIN se_privilege p ON p.privilege_id = rp.privilege_id " +
			"    JOIN user_paperless u ON u.user_id = ur.user_id " +
			"    LEFT JOIN company c ON c.cpn_id = u.cpn_id " +
			"    LEFT JOIN branch b ON b.branch_id = u.branch_id " +
			" WHERE LOWER(NVL(c.cpn_code,'-') || '\\' || NVL(b.branch_code,'-') || '\\' || u.user_login) = LOWER(?) "
		);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername : {}", username);
		UserDetails user = super.loadUserByUsername(username);

		Data data = userService.getUserInfo(username);
		return new TiffaUser(user,
				(Integer) data.get("userId"),
				(String) data.get("userLogin"),
				(String) data.get("displayName"),
				(Integer) data.get("companyId"),
				(String) data.get("companyCode"),
				(Integer) data.get("branchId"),
				(String) data.get("branchCode"),
				(Integer) data.get("isOfficer")
		);
	}

	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[]{username}, new int[] {Types.VARCHAR}, (rs, rowNum) -> {
			String userName = rs.getString(1);
			String password = rs.getString(2);
			boolean enabled = rs.getBoolean(3);

			boolean accLocked = false;
			boolean accExpired = false;
			boolean credsExpired = false;

			if (rs.getMetaData().getColumnCount() > 3) {
				//NOTE: acc_locked, acc_expired and creds_expired are also to be loaded
				accLocked = rs.getBoolean(4);
				accExpired = rs.getBoolean(5);
				credsExpired = rs.getBoolean(6);
			}
			return new User(userName, password, enabled, !accExpired, !credsExpired, !accLocked,
					AuthorityUtils.NO_AUTHORITIES);
		});
	}

	@Override
	protected void addCustomAuthorities(String username, List<GrantedAuthority> authorities) {
		if(authorities.size() == 0) {
			authorities.add(new SimpleGrantedAuthority("_MAIN_"));
		}
	}
	
	
}
