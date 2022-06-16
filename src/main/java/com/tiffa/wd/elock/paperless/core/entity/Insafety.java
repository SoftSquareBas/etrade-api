package com.tiffa.wd.elock.paperless.core.entity;

//import java.io.Serializable;
// import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;


@Data
@Entity(name = "in_safety")
@Table(name = "in_safety")
public class Insafety extends BaseEntity  {

    //private static final long serialVersionUID = 4460155681154593524L;


	@Id
	@Column(name = "item_code", length = 20)
	private String itemCode;

	

	@Column(name = "safety_qty", precision = 100)
    private Integer amountSafety;

	@Column(name = "active", length = 1)
    private String active;


    

    // @Column(name = "location_name", length = 100)
    // private String locationName;

    // @Column(name = "active", length = 1)
    // private String active;

    // @CreationTimestamp
    // @Column(name = "upd_date", length = 100)
    // private Timestamp updDate;


}
