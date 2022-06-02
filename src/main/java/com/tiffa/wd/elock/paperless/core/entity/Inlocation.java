package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;


@Data
@Entity(name = "in_location")
@Table(name = "in_location")
public class Inlocation implements Serializable {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    @Column(name = "ware_code", length = 10)
    private String ware_code;

    @Column(name = "location_code", length = 10)
    private String location_code;

    @Column(name = "location_name", length = 100)
    private String location_name;

    @Column(name = "active", length = 100)
    private String active;

    @CreationTimestamp
    @Column(name = "upd_date", length = 100)
    private Timestamp updDate;


}
