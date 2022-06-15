package com.tiffa.wd.elock.paperless.core.model;

import java.sql.Timestamp;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Rt06Model extends CommonRequest implements PageRequest {

    private String brandCode;

    private String brandName;

    private String brandNameEn;

    private String supplier;

    private String exporter;
    
    private String active;

    private Timestamp updDate;

}
