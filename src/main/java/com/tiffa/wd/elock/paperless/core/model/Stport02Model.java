package com.tiffa.wd.elock.paperless.core.model;

import java.sql.Timestamp;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Stport02Model extends CommonRequest implements PageRequest, ValidateRequest {

    private String poTypeCode;

    private String id;

    private String poTypeDesc;

    private String active;

    private String search;

    // private Timestamp updDate;

}
