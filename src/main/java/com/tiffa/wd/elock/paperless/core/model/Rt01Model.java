package com.tiffa.wd.elock.paperless.core.model;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString( callSuper = true)
public class Rt01Model extends CommonRequest implements PageRequest {
    
    private String ouCode;

    private String wareCode;

    private String wareName;

    private String active;

    private String saleIdBr;

    private String arCodeBr;



}
