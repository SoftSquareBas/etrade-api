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
@Entity(name = "in_warehouse")
@Table(name = "in_warehouse")

public class Inwarehouse implements Serializable {

    @Id
    @Column(name = "ou_code", length = 10 )
    private String ouCode;

    @Column(name = "ware_code", length = 10)
    private String wareCode;

    @Column(name = "ware_name", length = 100)
    private String wareName;

    @Column(name = "active", length = 1)
    private String active;

    @Column(name = "ar_code_br", length = 10)
    private String arCodeBr;

    @Column(name = "sale_id_br", length = 10)
    private String saleIdBr;


    // @CreationTimestamp
    // @Column(name = "upd_date")
    // private Timestamp updDate;
    



}
