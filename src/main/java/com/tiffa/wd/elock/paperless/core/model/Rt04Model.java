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
public class Rt04Model extends CommonRequest implements PageRequest {

    private String groupCode;

    private String groupName;

    private String categoryCode;

    private String active;

    private String categoryDesc;

    private String categoryDescEng;

    private Timestamp updDate;

}
