package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.LastModifiedDate;
//import com.ssru.back.admission.utils.CoreUtils;
//import com.ssru.back.admission.utils.UserUtils;
import com.tiffa.wd.elock.paperless.core.util.SecurityUtils;
import lombok.Data;
import com.tiffa.wd.elock.paperless.core.util.CoreUtils;
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

	private static final long serialVersionUID = -71246370776569091L;

	@Column(name = "cr_by", length = 20, updatable = false)
	private String crBy;

	@CreationTimestamp
	@Column(name = "cr_date", updatable = false)
	private Timestamp crDate;

	@Column(name = "prog_id", length = 20, updatable = false)
	private String progId;

	@Column(name = "upd_by", length = 20)
	private String updBy;

	@LastModifiedDate
	@Version
	@Column(name = "upd_date")
	private Timestamp updDate;

	@Column(name = "upd_prog_id", length = 20)
	private String updProgId;
	
	@PreUpdate
	public void preUpdate() {
		if (CoreUtils.isNull(updBy)) {
			updBy = SecurityUtils.getUsername();
		}
	}

	@PrePersist
	public void prePersist() {
		if (CoreUtils.isNull(crBy) && CoreUtils.isNull(updBy)) {
			crBy = SecurityUtils.getUsername();
			updBy = SecurityUtils.getUsername();
		}
	}
}
