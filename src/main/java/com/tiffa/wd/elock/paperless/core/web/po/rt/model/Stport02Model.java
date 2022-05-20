package com.tiffa.wd.elock.paperless.core.web.po.rt.model;

import com.tiffa.wd.elock.paperless.core.CommonRequest;
import com.tiffa.wd.elock.paperless.core.PageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Stport02Model extends CommonRequest implements PageRequest {

    private String poTypeCode;

    private String poTypeDesc;

    private String active;

}
