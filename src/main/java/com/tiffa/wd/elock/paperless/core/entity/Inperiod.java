package com.tiffa.wd.elock.paperless.core.entity;

import java.time.LocalDate;

//import java.io.Serializable;
// import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

// import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;


@Data
@Entity(name = "in_period")
@Table(name = "in_period")
public class Inperiod extends BaseEntity  {

    //private static final long serialVersionUID = 4460155681154593524L;


	@Id
	private InperiodPk pk;

    

    @Column(name = "start_date", length = 100)
    private LocalDate startDate;

    @Column(name = "end_date", length = 100)
    private LocalDate endDate;

    @Column(name = "real_status", length = 1)
    private String realStatus;

    @Column(name = "status_fg", length = 1)
    private String statusFg;

    @Column(name = "status_sp", length = 1)
    private String statusSp;

    // @CreationTimestamp
    // @Column(name = "upd_date", length = 100)
    // private Timestamp updDate;


}
