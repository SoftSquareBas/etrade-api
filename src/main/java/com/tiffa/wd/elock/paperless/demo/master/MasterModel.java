package com.tiffa.wd.elock.paperless.demo.master;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;
import com.tiffa.wd.elock.paperless.core.ValidateRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MasterModel extends CommonRequest implements PageRequest, ValidateRequest {

    private String poTypeCode;

    private String id;

    private String poTypeDesc;

    private String active;

    private String search;

    // private Timestamp updDate;

}
