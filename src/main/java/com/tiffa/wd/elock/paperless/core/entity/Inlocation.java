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
@Entity(name = "in_location")
@Table(name = "in_location")
public class Inlocation extends BaseEntity  {

    //private static final long serialVersionUID = 4460155681154593524L;


	@Id
	private InlocationPk pk;

    

    @Column(name = "location_name", length = 100)
    private String locationName;

    @Column(name = "active", length = 1)
    private String active;

    // @CreationTimestamp
    // @Column(name = "upd_date", length = 100)
    // private Timestamp updDate;


}
