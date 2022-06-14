package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Data
@Entity(name = "db_title")
@Table(name = "db_title")
public class DbTitle extends BaseEntity {

    private static final long serialVersionUID = 4460155681154593524L;

    @Id
    @Column(name = "title_code", length = 5)
    private String titleCode;

    @Column(name = "title_name_tha", length = 30)
    private String titleNameTha;

    @Column(name = "title_name_eng", length = 30)
    private String titleNameEng;

    @Column(name = "suffix_name_tha", length = 30)
    private String suffixNameTha;

    @Column(name = "suffix_name_eng", length = 30)
    private String suffixNameEng;

    @Column(name = "title_ini_tha", length = 30)
    private String titleIniTha;

    @Column(name = "title_ini_eng", length = 30)
    private String titleIniEng;

    @Column(name = "suffix_ini_tha", length = 30)
    private String suffixIniTha;

    @Column(name = "suffix_ini_eng", length = 30)
    private String suffixIniEng;

    @Column(name = "active", length = 1)
    private String active;

}
