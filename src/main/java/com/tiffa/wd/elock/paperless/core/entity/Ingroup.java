package com.tiffa.wd.elock.paperless.core.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity(name = "in_group")
@Table(name = "in_group")
public class Ingroup extends BaseEntity{
    
    @Id
    @Column(name = "group_code", length = 10 )
    private String groupCode;

    // @Column(name = "group_name", length = 100 )
    // private String groupName;


    @Column(name = "active", length = 1 )
    private String active;

    
    
}
