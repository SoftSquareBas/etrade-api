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
@Entity(name = "in_sub_cat")
@Table(name = "in_sub_cat")
public class Insubcat extends BaseEntity  {

    //private static final long serialVersionUID = 4460155681154593524L;


	@Id
	private InsubcatPk pk;

    

    @Column(name = "sub_cat_desc", length =200)
    private String categoreyThai;

    @Column(name = "sub_cat_desc_eng", length = 100)
    private String categoreyEng;

    @Column(name = "active", length = 1)
    private String active;

    // @CreationTimestamp
    // @Column(name = "upd_date", length = 100)
    // private Timestamp updDate;


}
