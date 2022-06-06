package com.tiffa.wd.elock.paperless.core.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class InwarehousePk implements Serializable {
    
    @Column(name = "ou_code", length = 10 )
    private String ouCode;

    @Column(name = "ware_code", length = 10)
    private String wareCode;
    
}
